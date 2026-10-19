package com.enchantea.application.ord.temp;

import com.enchantea.application.ord.contracts.RestaurantQuery;
import org.springframework.stereotype.Component;

/** TEMP defaults: open, available, one cent. Replace with Person B's real restaurant/menu query. */
@Component
public class TempRestaurantQuery implements RestaurantQuery {
    @Override
    public boolean isOpen(String restaurantId) {
        return true;
    }

    @Override
    public boolean isItemAvailable(String restaurantId, String itemId) {
        return true;
    }

    @Override
    public double itemPrice(String restaurantId, String itemId) {
        return 0.01;
    }
}
