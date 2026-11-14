package com.enchantea.application.reg;

import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.CourierStatus;
import com.enchantea.domain.reg.Location;

public class CourierResponse {
    private Long userId;
    private String username;
    private CourierStatus status;
    private Location position;

    public CourierResponse() {
    }

    public CourierResponse(
        Long userId,
        String username,
        CourierStatus status,
        Location position) {
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.position = position;
    }

    public static CourierResponse from(Courier courier) {
        return new CourierResponse(
                courier.getUserId(),
                courier.getUsername(),
                courier.getStatus(),
                courier.getPosition());
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public CourierStatus getStatus() {
        return status;
    }

    public Location getPosition() {
        return position;
    }
}

