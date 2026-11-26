package com.enchantea.domain.prc;

import java.util.Objects;

public final class Charge {
    private final double SUBTOTAL;
    private final Fees FEES;
    private final double DISCOUNT;
    private final double TOTAL;

    public Charge(double subtotal, Fees fees) {
        this(subtotal, fees, 0);
    }

    public Charge(double subtotal, Fees fees, double discount) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative");
        }
        if (discount < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }
        this.SUBTOTAL = subtotal;
        this.FEES = Objects.requireNonNull(fees);
        this.DISCOUNT = discount;
        this.TOTAL = subtotal + fees.total() - discount;
    }

    public double subtotal() { return SUBTOTAL; }
    public Fees fees() { return FEES; }
    public double deliveryFee() { return FEES.deliveryFee(); }
    public double serviceFee() { return FEES.serviceFee(); }
    public double smallOrderSurcharge() { return FEES.smallOrderSurcharge(); }
    public double discount() { return DISCOUNT; }
    public double total() { return TOTAL; }

    public Charge withDiscount(double discount) {
        return new Charge(SUBTOTAL, FEES, discount);
    }
}
