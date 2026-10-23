package com.enchantea.domain.ord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class OrderLineTests {
    @Test
    void totalIsUnitPriceTimesQuantity() {
        OrderLine line = new OrderLine("bubble-tea", 3, 5.5);

        assertEquals(16.5, line.total(), 0.0001);
    }

    @Test
    void rejectsAZeroOrNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new OrderLine("bubble-tea", 0, 5.0));
        assertThrows(IllegalArgumentException.class, () -> new OrderLine("bubble-tea", -1, 5.0));
    }

    @Test
    void rejectsANegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> new OrderLine("bubble-tea", 1, -0.01));
    }

    @Test
    void rejectsABlankItemId() {
        assertThrows(IllegalArgumentException.class, () -> new OrderLine(" ", 1, 5.0));
    }
}
