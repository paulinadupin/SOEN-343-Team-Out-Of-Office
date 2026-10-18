package com.enchantea.domain.reg;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User{
    private String name;

    @Embedded
    @AttributeOverride(name = "lat", column = @Column(name = "delivery_lat"))
    @AttributeOverride(name = "lng", column = @Column(name = "delivery_lng"))
    private Location deliveryAddress;

    protected Customer() {
    }

    public Customer(String username, String passwordHash, String name, Location deliveryAddress) {
        super(username, passwordHash);
        this.name = name;
        this.deliveryAddress = deliveryAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Location deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.CUSTOMER;
    }
}
