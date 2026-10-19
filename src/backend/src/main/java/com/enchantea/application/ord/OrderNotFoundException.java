package com.enchantea.application.ord;

//when an order isnt found
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) {
        super("Order not found: " + id);
    }
}
