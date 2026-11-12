package com.enchantea.application.dash;

import org.springframework.stereotype.Service;

import com.enchantea.application.ord.OrderRepository;
import com.enchantea.foundation.reg.UserRepository;

@Service
public class MapService {
    private final UserRepository USERS;
    private final OrderRepository ORDERS;

    public MapService(UserRepository users, OrderRepository orders) {
        this.USERS = users;
        this.ORDERS = orders;
    }

    public MapResponse restaurantMap(Long restaurantId) {
        
    }
}
