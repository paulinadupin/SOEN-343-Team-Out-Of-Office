package com.enchantea.domain.dlv;

// to throws an error when the transition attempted is illegal (R-DLV-03 & R-DLV-02 )
public class InvalidCourierTransitionException extends IllegalStateException {
    public InvalidCourierTransitionException(CourierStatus status, String action) {
        super("Courier cannot " + action + " in state " + status.name().toLowerCase());
    }
}