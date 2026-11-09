package com.enchantea.domain.prc;

public final class Charge {
    private final double SUBTOTAL;
    private final double DELIVERY_FEE;
    private final double SERVICE_FEE;
    private final double SMALL_ORDER_SURCHARGE;
    private final double TOTAL;

    public Charge(double subtotal, double deliveryFee, double serviceFee, double smallOrderSurcharge) {
        if (subtotal < 0 || deliveryFee < 0 || serviceFee < 0 || smallOrderSurcharge < 0) {
            throw new IllegalArgumentException("Charge amounts cannot be negative");
        }
        this.SUBTOTAL = subtotal;
        this.DELIVERY_FEE = deliveryFee;
        this.SERVICE_FEE = serviceFee;
        this.SMALL_ORDER_SURCHARGE = smallOrderSurcharge;
        this.TOTAL = subtotal + deliveryFee + serviceFee + smallOrderSurcharge;
    }

    public double subtotal() { return SUBTOTAL; }
    public double deliveryFee() { return DELIVERY_FEE; }
    public double serviceFee() { return SERVICE_FEE; }
    public double smallOrderSurcharge() { return SMALL_ORDER_SURCHARGE; }
    public double total() { return TOTAL; }
}
