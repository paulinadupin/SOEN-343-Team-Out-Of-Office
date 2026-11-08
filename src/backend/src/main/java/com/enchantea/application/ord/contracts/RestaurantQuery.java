package com.enchantea.application.ord.contracts;

import java.util.Optional;

import com.enchantea.domain.reg.Location;

public interface RestaurantQuery {
    boolean isOpen(String restaurantId);
    boolean isItemAvailable(String restaurantId, String itemId);
    double itemPrice(String restaurantId, String itemId);
    Optional<Location> location(String restaurantId);
}
