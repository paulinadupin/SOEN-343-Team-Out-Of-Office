package com.enchantea.domain.ord;

// to throws an error when the transition attempted is illegal (R-ORD-04)
public class InvalidOrderTransitionException extends IllegalStateException {
    public InvalidOrderTransitionException(OrderStatus status, String action) {
        super("Cannot " + action + " an order in state " + status.name().toLowerCase());
    }
}
