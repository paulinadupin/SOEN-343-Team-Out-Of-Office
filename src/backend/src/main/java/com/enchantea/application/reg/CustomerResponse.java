package com.enchantea.application.reg;

import com.enchantea.domain.reg.Customer;
import com.enchantea.domain.reg.Location;

public class CustomerResponse {
    private String name;
    private Location deliveryAddress;

    public CustomerResponse() {
    }

    public CustomerResponse(
        String name,
        Location deliveryAddress) {
        this.name = name;
        this.deliveryAddress = deliveryAddress;
    }

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getName(),
                customer.getDeliveryAddress());
    }


    public String getName() {
        return name;
    }

    public Location getDeliveryAddress() {
        return deliveryAddress;
    }
}
