package com.enchantea.application.dash;

import java.util.List;
import com.enchantea.application.reg.RestaurantResponse;
import com.enchantea.application.reg.CourierResponse;
import com.enchantea.application.reg.CustomerResponse;
import com.enchantea.domain.reg.Location;

public class MapResponse {
    private List<RestaurantResponse> restaurants;
    private List<CourierResponse> couriers;
    private CustomerResponse customer;

    public MapResponse() {}

    public MapResponse(
        List<RestaurantResponse> restaurants,
        List<CourierResponse> couriers,
        CustomerResponse customer)
        {
            this.restaurants = restaurants;
            this.couriers = couriers;
            this.customer = customer;
        }

    public List<RestaurantResponse> getRestaurants() {
        return restaurants;
    }

    public List <CourierResponse> getCouriers() {
        return couriers;
    }

    public CustomerResponse getCustomer() {
        return customer;
    }
}
