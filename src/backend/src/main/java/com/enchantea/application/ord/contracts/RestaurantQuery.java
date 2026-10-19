package com.enchantea.application.ord.contracts;

public interface RestaurantQuery {
    boolean isOpen(String restaurantId);
    boolean isItemAvailable(String restaurantId, String itemId);
    double itemPrice(String restaurantId, String itemId);
}
