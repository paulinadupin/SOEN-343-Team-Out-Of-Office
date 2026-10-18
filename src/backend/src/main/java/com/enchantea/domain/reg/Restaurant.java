package com.enchantea.domain.reg;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@DiscriminatorValue("RESTAURANT")
public class Restaurant extends User {
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="restaurant_status")
    private RestaurantStatus status;

    @Embedded
    @AttributeOverride(name = "lat", column = @Column(name = "restaurant_lat"))
    @AttributeOverride(name = "lng", column = @Column(name = "restaurant_lng"))
    private Location position;

    protected Restaurant() {
    }

    public Restaurant(String username, String passwordHash, String name, RestaurantStatus status, Location position) {
        super(username, passwordHash);
        this.name = name;
        this.status = status;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RestaurantStatus getStatus() {
        return status;
    }

    public void setStatus(RestaurantStatus status) {
        this.status = status;
    }

    public Location getPosition() {
        return position;
    }

    public void setPosition(Location position) {
        this.position = position;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.RESTAURANT;
    }
}
