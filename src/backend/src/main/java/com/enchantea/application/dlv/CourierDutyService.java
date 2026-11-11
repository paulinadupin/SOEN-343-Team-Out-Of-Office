package com.enchantea.application.dlv;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enchantea.application.ord.OrderApplicationService;
import com.enchantea.application.ord.OrderCommands;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.domain.dlv.Assignment;
import com.enchantea.domain.dlv.AssignmentStatus;
import com.enchantea.domain.dlv.Handoff;
import com.enchantea.domain.dlv.HandoffType;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.User;
import com.enchantea.foundation.reg.UserRepository;
import java.time.Instant;

@Service
public class CourierDutyService {
    private final UserRepository USER_REPOSITORY;
    private final OrderApplicationService ORDER_APPLICATION_SERVICE;
    private final HandoffRepository HANDOFF_REPOSITORY;

    public CourierDutyService(UserRepository userRepository, OrderApplicationService orderApplicationService, HandoffRepository handoffRepository) {
        this.USER_REPOSITORY = userRepository;
        this.ORDER_APPLICATION_SERVICE = orderApplicationService;
        this.HANDOFF_REPOSITORY = handoffRepository;
    }

    @Transactional
    public void goOnline(Long courierId) {
        Courier courier = requireCourier(courierId);
        if (courier.getPosition() == null) {
            throw new IllegalArgumentException("Courier does not have a valid location on the map");
        }

        courier.goOnline(); // State design pattern allows this action only from OFFLINE.
        USER_REPOSITORY.save(courier);
    }

    @Transactional
    public void goOffline(Long courierId) {
        Courier courier = requireCourier(courierId);

        Assignment assignment = courier.getAssignment();
        if (assignment != null && assignment.getStatus() == AssignmentStatus.OFFERED) {
            assignment.declineOffer();
            courier.setAssignment(null);
        }

        courier.goOffline(); // State design pattern allows this action only from IDLE.
        USER_REPOSITORY.save(courier);
    }

    @Transactional
    public void acceptOffer(Long courierId) {
        Courier courier = requireCourier(courierId);
        Assignment assignment = requireOfferedAssignment(courier);

        if (assignment.isExpired()) {  //checks if time allocated is up
            returnOffer(courier, assignment);
            return;
        }

        courier.acceptOffer(); // State design pattern allows this action only from IDLE.
        assignment.acceptOffer();
        USER_REPOSITORY.save(courier);
    }

    @Transactional
    public void declineOffer(Long courierId) {
        Courier courier = requireCourier(courierId);
        Assignment assignment = requireOfferedAssignment(courier);

        courier.declineOffer(); // IDLE remains IDLE. other states reject the action.
        returnOffer(courier, assignment);
    }

    @Transactional
    public void confirmPickup(Long courierId, String orderId) {
        Courier courier = requireCourier(courierId);
        Assignment assignment = courier.getAssignment();

        if (assignment == null
                || assignment.getStatus() != AssignmentStatus.ACCEPTED
                || assignment.getOrder() == null
                || !assignment.getOrder().id().equals(orderId)) {

            throw new IllegalStateException("Order is not assigned to this courier");
        }

        courier.confirmPickup(); // State design pattern allows this action only from ASSIGNED.
        ORDER_APPLICATION_SERVICE.pickUp(currentUser(courier), new OrderCommands.OrderAction(orderId, null));
        HANDOFF_REPOSITORY.save(new Handoff(orderId, courier.getUserId(), HandoffType.PICKUP, Instant.now()));
        USER_REPOSITORY.save(courier);
    }

    @Transactional
    public void confirmDelivery(Long courierId, Long customerId) {
        Courier courier = requireCourier(courierId);
        Assignment assignment = courier.getAssignment();

        if (assignment == null 
            || assignment.getStatus() != AssignmentStatus.ACCEPTED 
            || assignment.getOrder() == null) {

            throw new IllegalStateException("Courier has no accepted order");
        }

        Order order = assignment.getOrder();
        if (!order.customerId().equals(customerId)) {
            throw new IllegalArgumentException(
                    "Customer ID does not match the customer of the assigned order");
        }

        courier.confirmDelivery(); // State design pattern allows this action only from CARRYING.
        ORDER_APPLICATION_SERVICE.deliver(currentUser(courier), new OrderCommands.OrderAction(order.id(), null));
        HANDOFF_REPOSITORY.save(new Handoff(order.id(), courier.getUserId(), HandoffType.DELIVERY, Instant.now()));
        courier.setAssignment(null);
        USER_REPOSITORY.save(courier);
    }


    // --- Helper methods ---
    
    private Courier requireCourier(Long courierId) {
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!(user instanceof Courier courier)) { // if the user is a courier
            throw new IllegalArgumentException("User is not a courier");
        }

        return courier;
    }

    private Assignment requireOfferedAssignment(Courier courier) {
        Assignment assignment = courier.getAssignment();

        if (assignment == null || assignment.getStatus() != AssignmentStatus.OFFERED) { //assignment checks
            throw new IllegalStateException("Courier has no offer to respond to");
        }

        return assignment;
    }

    private void returnOffer(Courier courier, Assignment assignment) { //when the courier has an offer but wants to go offline
        assignment.declineOffer();
        courier.setAssignment(null);
        USER_REPOSITORY.save(courier);
    }

    private static CurrentUser currentUser(Courier courier) { //to recieve the current user
        return new CurrentUser(courier.getUserId(), AccountType.COURIER, null);
    }
}
