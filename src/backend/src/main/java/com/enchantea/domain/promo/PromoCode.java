package com.enchantea.domain.promo;

import java.time.Instant;

public record PromoCode(String code, Instant expiresAt, double minOrderValue, double percentOff) {
    public PromoCode {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("A promo code must have a value");
        }
        if (minOrderValue < 0) {
            throw new IllegalArgumentException("Minimum order value cannot be negative");
        }
        if (percentOff <= 0 || percentOff > 100) {
            throw new IllegalArgumentException("Percent off must be between 0 and 100");
        }
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public boolean meetsMinimumOrderValue(double subtotal) {
        return subtotal >= minOrderValue;
    }

    public double discountFor(double subtotal) {
        return subtotal * (percentOff / 100.0);
    }
}
