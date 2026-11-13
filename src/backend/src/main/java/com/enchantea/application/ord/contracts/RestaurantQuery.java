package com.enchantea.application.ord.contracts;

import java.util.Optional;

import com.enchantea.domain.reg.Location;

public interface RestaurantQuery {
    boolean isOpen(Long restaurantId);
    boolean isItemAvailable(Long restaurantId, String itemId);
    double itemPrice(Long restaurantId, String itemId);
    Optional<Location> location(Long restaurantId);

    // FIX: added for OrderSummaryService. Default methods keep existing implementations compiling;
    // override them in the class that implements this interface to return the real names.
    default Optional<String> restaurantName(Long restaurantId) {
        return Optional.empty();
    }

    default Optional<String> itemName(Long restaurantId, String itemId) {
        return Optional.empty();
    }
}