package com.enchantea.application.dlv;

import org.springframework.stereotype.Service;

import com.enchantea.foundation.reg.UserRepository;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.User;

@Service 
public class CourierDutyService {
    private final UserRepository USER_REPOSITORY;

    public CourierDutyService(UserRepository USER_REPOSITORY) {
        this.USER_REPOSITORY = USER_REPOSITORY;
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

        if (courier.getPosition() == null){
            throw new IllegalArgumentException("Courier does not have a valid location on the map");
        }

        courier.goOffline();
        USER_REPOSITORY.save(courier);

    }


    public void acceptOffer(Long courierId){
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        courier.acceptOffer();
        USER_REPOSITORY.save(courier);
    }

    public void declineOffer(Long courierId){
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        courier.declineOffer();
        USER_REPOSITORY.save(courier);

    }

    public void confirmPickup(Long courierId){
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        courier.confirmPickup();
        USER_REPOSITORY.save(courier);

    }

    public void confirmDelivery(Long courierId){
        User user = USER_REPOSITORY.findById(courierId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Courier courier)) {
            throw new IllegalArgumentException("User is not a courier");
        }

        courier.confirmDelivery();
        USER_REPOSITORY.save(courier);
    }

}
