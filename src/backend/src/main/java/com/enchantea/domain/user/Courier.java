package com.enchantea.domain.user;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@DiscriminatorValue("COURIER")
public class Courier extends User{
    @Enumerated(EnumType.STRING)
    @Column(name="courier_status")
    private CourierStatus status;

    @Embedded
    @AttributeOverride(name = "lat", column = @Column(name = "courier_lat"))
    @AttributeOverride(name = "lng", column = @Column(name = "courier_lng"))
    private Location position;

    protected Courier() {
    }

    public Courier(String username, String passwordHash, CourierStatus status, Location position) {
        super(username, passwordHash);
        this.status = status;
        this.position = position;
    }

    public CourierStatus getStatus() {
        return status;
    }

    public void setStatus(CourierStatus status) {
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
        return AccountType.COURIER;
    }
}
