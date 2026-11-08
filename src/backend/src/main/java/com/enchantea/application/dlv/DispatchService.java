package com.enchantea.application.dlv;

import java.util.Optional;

import com.enchantea.application.ord.contracts.DispatchQueue;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.Location;

public class DispatchService {
    private final DispatchQueue DISPATCH_QUEUE;
    private final CourierSelector COURIER_SELECTOR;
    private final RestaurantQuery RESTAURANT_QUERY;

    public DispatchService(DispatchQueue DISPATCH_QUEUE, CourierSelector COURIER_SELECTOR, RestaurantQuery RESTAURANT_QUERY) {
        this.DISPATCH_QUEUE = DISPATCH_QUEUE;
        this.COURIER_SELECTOR = COURIER_SELECTOR;
        this.RESTAURANT_QUERY = RESTAURANT_QUERY;
    }

    public void dispatchNext(){
        Optional<Order> next = DISPATCH_QUEUE.peek(); //get first element in dispatch queue (will return first order or null if queue is empty)
        
        //if no order in queue
        if (next.isEmpty()){
            return ;
        }

        Order order = next.get();
        Optional<Location> location = RESTAURANT_QUERY.location(order.restaurantId()); //get restaurant location

        //if restaurant location not found
        if (location.isEmpty()){
            return ;
        }

        // get selected courier
        Courier selectedCourier = COURIER_SELECTOR.select(location.get());
        
        //TODO: implement sending offer

    }
}
