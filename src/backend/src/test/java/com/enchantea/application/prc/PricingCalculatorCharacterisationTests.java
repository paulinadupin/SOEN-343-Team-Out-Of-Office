package com.enchantea.application.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.enchantea.domain.ord.OrderLine;
import com.enchantea.domain.prc.Charge;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class PricingCalculatorCharacterisationTests {

    private PricingCalculator calculator;

    @BeforeEach
    void setUp() throws Exception {
        PricingConfigLoader loader = new PricingConfigLoader(JsonMapper.builder().build());
        loader.load();
        calculator = new PricingCalculator(loader);
    }

    private static void assertCharge(Charge charge, double subtotal, double deliveryFee,
            double serviceFee, double surcharge, double total) {
        assertEquals(subtotal, charge.subtotal(), 0.0001);
        assertEquals(deliveryFee, charge.deliveryFee(), 0.0001);
        assertEquals(serviceFee, charge.serviceFee(), 0.0001);
        assertEquals(surcharge, charge.smallOrderSurcharge(), 0.0001);
        assertEquals(total, charge.total(), 0.0001);
    }

    @Test
    void smallOrderGetsEveryFeeAndTheSurcharge() {
        Charge charge = calculator.computeCharge(List.of(new OrderLine("bubble-tea", 1, 5.0)));

        assertCharge(charge, 5.00, 2.99, 1.50, 2.00, 11.49);
    }

    @Test
    void orderAtTheThresholdSkipsTheSurcharge() {
        Charge charge = calculator.computeCharge(List.of(new OrderLine("bubble-tea", 3, 5.0)));

        assertCharge(charge, 15.00, 2.99, 1.50, 0.00, 19.49);
    }

    @Test
    void largeOrderSkipsTheSurcharge() {
        Charge charge = calculator.computeCharge(List.of(
                new OrderLine("bubble-tea", 2, 5.0),
                new OrderLine("taro-milk-tea", 2, 6.0)));

        assertCharge(charge, 22.00, 2.99, 1.50, 0.00, 26.49);
    }

    @Test
    void emptyOrderStillPaysTheFeesAndSurcharge() {
        Charge charge = calculator.computeCharge(List.of());

        assertCharge(charge, 0.00, 2.99, 1.50, 2.00, 6.49);
    }
}