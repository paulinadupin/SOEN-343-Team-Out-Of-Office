package com.enchantea.application.dlv;

import com.enchantea.application.ord.OrderRepository;
import com.enchantea.application.ord.contracts.CourierAssignment;
import com.enchantea.application.ord.contracts.DispatchQueue;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.domain.dlv.Assignment;
import com.enchantea.domain.dlv.AssignmentStatus;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.CourierStatus;
import com.enchantea.domain.reg.Location;
import com.enchantea.foundation.reg.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DispatchService implements CourierAssignment {
    private final DispatchQueue DISPATCH_QUEUE;
    private final CourierSelector COURIER_SELECTOR;
    private final RestaurantQuery RESTAURANT_QUERY;
    private final UserRepository USER_REPOSITORY;
    private final OrderRepository ORDER_REPOSITORY;

    public DispatchService(
            DispatchQueue dispatchQueue,
            CourierSelector courierSelector,
            RestaurantQuery restaurantQuery,
            UserRepository userRepository,
            OrderRepository orderRepository) {

        this.DISPATCH_QUEUE = dispatchQueue;
        this.COURIER_SELECTOR = courierSelector;
        this.RESTAURANT_QUERY = restaurantQuery;
        this.USER_REPOSITORY = userRepository;
        this.ORDER_REPOSITORY = orderRepository;
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
