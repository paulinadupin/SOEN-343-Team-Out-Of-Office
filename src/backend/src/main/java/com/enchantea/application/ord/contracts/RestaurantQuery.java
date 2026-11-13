package com.enchantea.application.ord.contracts;

import com.enchantea.domain.reg.Location;
import java.util.Optional;

public interface RestaurantQuery {
    boolean isOpen(Long restaurantId);
    boolean isItemAvailable(Long restaurantId, String itemId);
    double itemPrice(Long restaurantId, String itemId);
    Optional<Location> location(Long restaurantId);
    Optional<String> restaurantName(Long restaurantId);
    Optional<String> itemName(Long restaurantId, String itemId);
}