package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.domain.ord.MenuItem;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.RestaurantStatus;
import com.enchantea.foundation.reg.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CityRestaurantQuery implements RestaurantQuery {
    private final UserRepository users;
    private final MenuCatalog menuCatalog;

    public CityRestaurantQuery(UserRepository users, MenuCatalog menuCatalog) {
        this.users = users;
        this.menuCatalog = menuCatalog;
    }

    @Override
    public boolean isOpen(String restaurantId) {
        return findRestaurant(restaurantId)
                .map(restaurant -> restaurant.getStatus() == RestaurantStatus.OPEN)
                .orElse(false);
    }

    @Override
    public boolean isItemAvailable(String restaurantId, String itemId) {
        return menuCatalog.find(restaurantId, itemId)
                .map(MenuItem::isAvailable)
                .orElse(false);
    }

    @Override
    public double itemPrice(String restaurantId, String itemId) {
        return menuCatalog.find(restaurantId, itemId)
                .map(MenuItem::price)
                .orElseThrow(() -> new CommandRejectedException("Unknown menu item: " + itemId));
    }

    private Optional<Restaurant> findRestaurant(String restaurantId) {
        try {
            return users.findById(Long.valueOf(restaurantId))
                    .filter(Restaurant.class::isInstance)
                    .map(Restaurant.class::cast);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
