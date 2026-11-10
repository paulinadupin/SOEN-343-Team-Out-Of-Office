package com.enchantea.application.dlv;

import org.springframework.stereotype.Service;

import com.enchantea.foundation.reg.UserRepository;
import com.enchantea.application.ord.OrderApplicationService;
import com.enchantea.application.ord.OrderCommands;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.domain.dlv.Assignment;
import com.enchantea.domain.dlv.AssignmentStatus;
import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.User;

@Service 
public class CourierDutyService {
    private final UserRepository USER_REPOSITORY;
    private final OrderApplicationService ORDER_APPLICATION_SERVICE;

    public CourierDutyService(UserRepository USER_REPOSITORY, OrderApplicationService ORDER_APPLICATION_SERVICE) {
        this.USER_REPOSITORY = USER_REPOSITORY;
        this.ORDER_APPLICATION_SERVICE = ORDER_APPLICATION_SERVICE;
    }

    public void goOnline(Long courierId) {
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        if (courier.getPosition() == null){
            throw new IllegalArgumentException("Courier does not have a valid location on the map");
        }

        courier.goOnline();
        USER_REPOSITORY.save(courier);
    }

    public void goOffline(Long courierId){
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        courier.goOffline();
        USER_REPOSITORY.save(courier);

    }


    public void acceptOffer(Long courierId){
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        if(courier.getAssignment() == null){
            throw new IllegalStateException("Courier has no assignment to accept");
        }

        if(courier.getAssignment().isExpired()){
            declineOffer(courierId);
            return;
        }

        courier.acceptOffer();
        courier.getAssignment().acceptOffer();
        USER_REPOSITORY.save(courier);
    }

    public void declineOffer(Long courierId){
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        if(courier.getAssignment() == null){
            throw new IllegalStateException("Courier has no assignment to accept");
        }

        courier.declineOffer();
        courier.getAssignment().declineOfffer();
        USER_REPOSITORY.save(courier);

    }

    public void confirmPickup(Long courierId, String orderId){
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        Assignment assignment = courier.getAssignment();

        //checks if the order the courier is trying to pick up, corresponds to the order it's assigned to
        if (assignment == null || assignment.getStatus() != AssignmentStatus.ACCEPTED || !assignment.getOrder().id().equals(orderId)){
            throw new IllegalStateException("Order is not assigned to this courier");
        }

        ORDER_APPLICATION_SERVICE.pickUp(
            new CurrentUser(user.getUserId().toString(), AccountType.COURIER, null), 
            new OrderCommands.OrderAction(orderId.toString(), null) 
        );

        courier.confirmPickup();
        USER_REPOSITORY.save(courier);

    }

    public void confirmDelivery(Long courierId, String customerId){
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        if(!(courier.getAssignment().getOrder().customerId().equals(customerId))){
            throw new IllegalArgumentException("Customer ID does not match the customer ID of the assigned order");
        }

        courier.confirmDelivery();
        USER_REPOSITORY.save(courier);
    }

}
