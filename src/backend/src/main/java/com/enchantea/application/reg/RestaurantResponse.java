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
    private int queueSize;

    public RestaurantResponse() {
    }

    public RestaurantResponse(
            Long userId,
            String username,
            String name,
            RestaurantStatus status,
            Location position,
            int queueSize
        ) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.status = status;
        this.position = position;
        this.queueSize = queueSize;
    }

    public static RestaurantResponse from(Restaurant restaurant, int queueSize) {
        return new RestaurantResponse(
                restaurant.getUserId(),
                restaurant.getUsername(),
                restaurant.getName(),
                restaurant.getStatus(),
                restaurant.getPosition(),
                queueSize);
    }

    public static RestaurantResponse from(Restaurant restaurant) {
        return from(restaurant, 0);
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

    public int getQueueSize() {
        return queueSize;
    }
}
