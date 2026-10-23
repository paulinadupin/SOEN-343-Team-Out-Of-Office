package com.enchantea.application.reg;

import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.RestaurantStatus;

public class RestaurantResponse {
    private Long userId;
    private String username;
    private String name;
    private RestaurantStatus status;
    private Location position;

    public RestaurantResponse() {
    }

    public RestaurantResponse(
            Long userId,
            String username,
            String name,
            RestaurantStatus status,
            Location position) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.status = status;
        this.position = position;
    }

    public static RestaurantResponse from(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getUserId(),
                restaurant.getUsername(),
                restaurant.getName(),
                restaurant.getStatus(),
                restaurant.getPosition());
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public RestaurantStatus getStatus() {
        return status;
    }

    public Location getPosition() {
        return position;
    }
}
