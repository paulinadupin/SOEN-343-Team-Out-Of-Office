package com.enchantea.domain.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ChargeTests {

    @Test
    void totalIsTheSumOfAllFourAmounts() {
        Charge charge = new Charge(10.0, 2.99, 1.50, 2.00);

        assertEquals(16.49, charge.total(), 0.0001);
    }

    @Test
    void rejectsANegativeSubtotal() {
        assertThrows(IllegalArgumentException.class, () -> new Charge(-1, 2.99, 1.50, 2.00));
    }

    @Test
    void rejectsANegativeDeliveryFee() {
        assertThrows(IllegalArgumentException.class, () -> new Charge(10.0, -1, 1.50, 2.00));
    }

    @Test
    void rejectsANegativeServiceFee() {
        assertThrows(IllegalArgumentException.class, () -> new Charge(10.0, 2.99, -1, 2.00));
    }

    @Test
    void rejectsANegativeSmallOrderSurcharge() {
        assertThrows(IllegalArgumentException.class, () -> new Charge(10.0, 2.99, 1.50, -1));
    }
}