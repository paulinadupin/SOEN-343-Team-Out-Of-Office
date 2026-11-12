package com.enchantea.application.ord.contracts;
import java.util.Optional;

import java.util.Optional;

import com.enchantea.domain.reg.Location;

public interface RestaurantQuery {
    boolean isOpen(Long restaurantId);
    boolean isItemAvailable(Long restaurantId, String itemId);
    double itemPrice(Long restaurantId, String itemId);
    Optional<Location> location(Long restaurantId);
}
