package com.enchantea.foundation.ord;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PersistedCharge {
    @Column(name = "charge_subtotal")
    private double subtotal;
    @Column(name = "charge_delivery_fee")
    private double deliveryFee;
    @Column(name = "charge_service_fee")
    private double serviceFee;
    @Column(name = "charge_small_order_surcharge")
    private double smallOrderSurcharge;

    protected PersistedCharge() {
    }

    public PersistedCharge(double subtotal, double deliveryFee, double serviceFee, double smallOrderSurcharge) {
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.serviceFee = serviceFee;
        this.smallOrderSurcharge = smallOrderSurcharge;
    }

    public double subtotal() { return subtotal; }
    public double deliveryFee() { return deliveryFee; }
    public double serviceFee() { return serviceFee; }
    public double smallOrderSurcharge() { return smallOrderSurcharge; }
}
