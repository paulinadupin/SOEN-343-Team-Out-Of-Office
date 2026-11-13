package com.enchantea.application.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.enchantea.domain.ord.OrderLine;
import com.enchantea.domain.prc.Charge;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class PricingCalculatorTests {

    private PricingCalculator calculator;

    @BeforeEach
    void setUp() throws Exception {
        PricingConfigLoader loader = new PricingConfigLoader(JsonMapper.builder().build());
        loader.load();
        calculator = new PricingCalculator(loader);
    }

    @Test
    void addsTheSurchargeWhenTheSubtotalIsBelowTheThreshold() {
        // pricing.json: smallOrderThreshold = 15.00, smallOrderSurcharge = 2.00
        List<OrderLine> lines = List.of(new OrderLine("bubble-tea", 1, 5.0)); // subtotal = 5.0

        Charge charge = calculator.computeCharge(lines);

        assertEquals(2.00, charge.smallOrderSurcharge(), 0.0001);
    }

    @Test
    void skipsTheSurchargeWhenTheSubtotalMeetsTheThreshold() {
        List<OrderLine> lines = List.of(new OrderLine("bubble-tea", 3, 5.0)); // subtotal = 15.0

        Charge charge = calculator.computeCharge(lines);

        assertEquals(0.0, charge.smallOrderSurcharge(), 0.0001);
    }

    @Test
    void subtotalIsTheSumOfAllLineTotals() {
        List<OrderLine> lines = List.of(
            new OrderLine("bubble-tea", 2, 5.0),
            new OrderLine("taro-milk-tea", 1, 6.0)
        );

        Charge charge = calculator.computeCharge(lines);

        assertEquals(16.0, charge.subtotal(), 0.0001);
    }
}