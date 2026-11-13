package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.domain.ord.MenuItem;
import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.RestaurantStatus;
import com.enchantea.foundation.reg.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CityRestaurantQuery implements RestaurantQuery {
    private final UserRepository USERS;
    private final MenuCatalog MENU_CATALOG;

    public CityRestaurantQuery(UserRepository USERS, MenuCatalog MENU_CATALOG) {
        this.USERS = USERS;
        this.MENU_CATALOG = MENU_CATALOG;
    }

    @Override
    public boolean isOpen(Long restaurantId) {
        return findRestaurant(restaurantId)
                .map(restaurant -> restaurant.getStatus() == RestaurantStatus.OPEN)
                .orElse(false);
    }

    @Override
    public boolean isItemAvailable(Long restaurantId, String itemId) {
        return MENU_CATALOG.find(restaurantId, itemId)
                .map(MenuItem::isAvailable)
                .orElse(false);
    }

    @Override
    public double itemPrice(Long restaurantId, String itemId) {
        return MENU_CATALOG.find(restaurantId, itemId)
                .map(MenuItem::price)
                .orElseThrow(() -> new CommandRejectedException("Unknown menu item: " + itemId));
    }

    private Optional<Restaurant> findRestaurant(Long restaurantId) {
        if (restaurantId == null) {
            return Optional.empty();
        }
        return USERS.findById(restaurantId)
                .filter(Restaurant.class::isInstance)
                .map(Restaurant.class::cast);
    }

    @Override
    public Optional<Location> location(Long restaurantId) {
        return findRestaurant(restaurantId).map(Restaurant::getPosition);
    }

    @Override
    public Optional<String> restaurantName(Long restaurantId) {
        return findRestaurant(restaurantId).map(Restaurant::getName);
    }

    @Override
    public Optional<String> itemName(Long restaurantId, String itemId) {
        return MENU_CATALOG.find(restaurantId, itemId).map(MenuItem::name);
    }
}