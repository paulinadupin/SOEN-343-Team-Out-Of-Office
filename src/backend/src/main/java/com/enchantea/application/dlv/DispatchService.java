package com.enchantea.application.dlv;

import com.enchantea.application.ord.OrderRepository;
import com.enchantea.application.ord.contracts.CourierAssignment;
import com.enchantea.application.ord.contracts.DispatchQueue;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.domain.dlv.Assignment;
import com.enchantea.domain.dlv.AssignmentStatus;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.CourierStatus;
import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.User;
import com.enchantea.foundation.reg.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class DispatchService implements CourierAssignment {
    private final DispatchQueue DISPATCH_QUEUE;
    private final CourierSelector COURIER_SELECTOR;
    private final RestaurantQuery RESTAURANT_QUERY;
    private final UserRepository USER_REPOSITORY;
    private final OrderRepository ORDER_REPOSITORY;
    private final TaskScheduler TASK_SCHEDULER;
    private final TransactionTemplate TRANSACTION_TEMPLATE;

    public DispatchService(
            DispatchQueue dispatchQueue,
            CourierSelector courierSelector,
            RestaurantQuery restaurantQuery,
            UserRepository userRepository,
            OrderRepository orderRepository,
            TaskScheduler taskScheduler,
            PlatformTransactionManager transactionManager) {

        this.DISPATCH_QUEUE = dispatchQueue;
        this.COURIER_SELECTOR = courierSelector;
        this.RESTAURANT_QUERY = restaurantQuery;
        this.USER_REPOSITORY = userRepository;
        this.ORDER_REPOSITORY = orderRepository;
        this.TASK_SCHEDULER = taskScheduler;
        this.TRANSACTION_TEMPLATE = new TransactionTemplate(transactionManager);
    }

    @Transactional
    public synchronized void dispatchNext() {
        List<Courier> availableCouriers = COURIER_SELECTOR.idleCouriers();
        while (!availableCouriers.isEmpty()) {
            Optional<Order> next = DISPATCH_QUEUE.peek();
            if (next.isEmpty()) {
                return;
            }

            Order order = next.get();
            Optional<Location> restaurantLocation = RESTAURANT_QUERY.location(order.restaurantId());
            if (restaurantLocation.isEmpty()) {
                return;
            }

            Courier courier = COURIER_SELECTOR.select(restaurantLocation.get(), availableCouriers);
            if (courier == null) {
                return;
            }

            availableCouriers.remove(courier);
            Assignment assignment = new Assignment(AssignmentStatus.OFFERED, order.id(), Instant.now());
            courier.setAssignment(assignment);
            USER_REPOSITORY.save(courier);
            DISPATCH_QUEUE.removeFromDispatch(order.id());
            scheduleOfferExpiry(courier.getUserId(), assignment);
        }
    }

    @Transactional
    public void requeueAndDispatch(String orderId) { // for when the offer is declined or expired
        Order order = ORDER_REPOSITORY.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        if (!DISPATCH_QUEUE.contains(orderId)) {
            DISPATCH_QUEUE.add(order);
        }
        dispatchNext();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void scheduleExistingOfferExpirations() {
        USER_REPOSITORY.findAll().stream()
                .filter(Courier.class::isInstance)
                .map(Courier.class::cast)
                .filter(courier -> courier.getAssignment() != null)
                .filter(courier -> courier.getAssignment().getStatus() == AssignmentStatus.OFFERED)
                .forEach(courier -> scheduleOfferExpiry(courier.getUserId(), courier.getAssignment()));
    }

    private void scheduleOfferExpiry(Long courierId, Assignment assignment) {
        String orderId = assignment.getOrderId();
        Instant offeredAt = assignment.getOfferedAt();
        Instant expiresAt = assignment.getExpiresAt();
        TASK_SCHEDULER.schedule(
                () -> TRANSACTION_TEMPLATE.executeWithoutResult(
                        transaction -> expireOffer(courierId, orderId, offeredAt)),
                expiresAt);
    }

    private synchronized void expireOffer(Long courierId, String orderId, Instant offeredAt) {
        User user = USER_REPOSITORY.findById(courierId).orElse(null);
        if (!(user instanceof Courier courier)) {
            return;
        }

        Assignment assignment = courier.getAssignment();
        if (assignment == null
                || assignment.getStatus() != AssignmentStatus.OFFERED
                || !orderId.equals(assignment.getOrderId())
                || !offeredAt.equals(assignment.getOfferedAt())
                || !assignment.isExpired()) {
            return;
        }

        assignment.declineOffer();
        courier.setAssignment(null);
        USER_REPOSITORY.save(courier);

        ORDER_REPOSITORY.findById(orderId)
                .filter(order -> order.status() == OrderStatus.READY)
                .filter(order -> !DISPATCH_QUEUE.contains(orderId))
                .ifPresent(DISPATCH_QUEUE::add);
        dispatchNext();
    }

    @Override
    @Transactional
    public void removeOrderAssignment(String orderId) { // for when operator cancels order
        List<Courier> couriers = USER_REPOSITORY.findAll().stream()
                .filter(Courier.class::isInstance)
                .map(Courier.class::cast)
                .filter(courier -> courier.getAssignment() != null
                        && orderId.equals(courier.getAssignment().getOrderId()))
                .toList();

        for (Courier courier : couriers) {
            if (courier.getStatus() == CourierStatus.ASSIGNED
                    || courier.getStatus() == CourierStatus.CARRYING) {
                courier.operatorCancelAssignment();
            }
            courier.setAssignment(null);
            USER_REPOSITORY.save(courier);
        }
        if (!couriers.isEmpty()) {
            dispatchNext();
        }
    }
}
