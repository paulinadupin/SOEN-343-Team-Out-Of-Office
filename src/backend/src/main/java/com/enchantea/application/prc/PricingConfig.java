package com.enchantea.application.prc;

public record PricingConfig(
        double deliveryFee,
        double serviceFee,
        double smallOrderThreshold,
        double smallOrderSurcharge) { }
