package com.enchantea.application.dlv;

import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.CourierStatus;
import com.enchantea.domain.reg.Location;
import com.enchantea.foundation.reg.UserRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CourierSelector {
    private final UserRepository USER_REPOSITORY;

    public CourierSelector(UserRepository userRepository) {
        this.USER_REPOSITORY = userRepository;
    }

    public List<Courier> idleCouriers() {
        return USER_REPOSITORY.findAll().stream()
                .filter(Courier.class::isInstance)
                .map(Courier.class::cast)
                .filter(courier -> courier.getStatus() == CourierStatus.IDLE)
                .filter(courier -> courier.getPosition() != null)
                .filter(courier -> courier.getAssignment() == null)
                .toList();
    }

    public Courier select(Location restaurantLocation) {
        return select(restaurantLocation, idleCouriers());
    }

    public Courier select(Location restaurantLocation, List<Courier> candidates) {
        if (restaurantLocation == null) {
            throw new IllegalArgumentException("Restaurant location is required");
        }

        return candidates.stream()
                .min(Comparator
                        .comparingDouble((Courier courier) -> distance(courier.getPosition(), restaurantLocation))
                        .thenComparing(Courier::getUserId))
                .orElse(null);
    }

    private static double distance(Location first, Location second) {
        return Math.hypot(first.getLat() - second.getLat(), first.getLng() - second.getLng());
    }
}
