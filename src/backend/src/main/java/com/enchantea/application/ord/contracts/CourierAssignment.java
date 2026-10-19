package com.enchantea.application.ord.contracts;

/** Removes an order from its assigned courier, if it currently has one. */
public interface CourierAssignment {
    void removeOrderAssignment(String orderId);
}
