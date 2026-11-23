package com.enchantea.domain.prc;

public record Fees(double deliveryFee, double serviceFee, double smallOrderSurcharge) {
    public Fees {
        if (deliveryFee < 0 || serviceFee < 0 || smallOrderSurcharge < 0) {
            throw new IllegalArgumentException("Fees cannot be negative");
        }
    }

    public double total() {
        return deliveryFee + serviceFee + smallOrderSurcharge;
    }
}