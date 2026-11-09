package com.enchantea.application.ord.contracts;
import java.util.Optional;

public interface RestaurantQuery {
    boolean isOpen(String restaurantId);
    boolean isItemAvailable(String restaurantId, String itemId);
    double itemPrice(String restaurantId, String itemId);
    Optional<String> restaurantName(String restaurantId);
    Optional<String> itemName(String restaurantId, String itemId);
}
