package com.enchantea.domain.prc;

import java.util.Objects;

public final class Charge {
    private final double SUBTOTAL;
    private final Fees FEES;
    private final double TOTAL;

    public Charge(double subtotal, Fees fees) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative");
        }
        this.SUBTOTAL = subtotal;
        this.FEES = Objects.requireNonNull(fees);
        this.TOTAL = subtotal + fees.total();
    }

    public double subtotal() { return SUBTOTAL; }
    public Fees fees() { return FEES; }
    public double deliveryFee() { return FEES.deliveryFee(); }
    public double serviceFee() { return FEES.serviceFee(); }
    public double smallOrderSurcharge() { return FEES.smallOrderSurcharge(); }
    public double total() { return TOTAL; }
}
