package com.enchantea.application.ord;

import com.enchantea.domain.ord.MenuItemStatus;
import com.enchantea.domain.reg.RestaurantStatus;
import java.util.List;

public record CityConfig(List<RestaurantConfig> restaurants) {
    public record RestaurantConfig(String username, RestaurantStatus status, List<MenuItemConfig> menu) { }

    public record MenuItemConfig(String id, String name, double price, MenuItemStatus status) { }
}
