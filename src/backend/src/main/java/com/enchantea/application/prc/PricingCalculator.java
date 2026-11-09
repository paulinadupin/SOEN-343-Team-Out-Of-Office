package com.enchantea.application.prc;

import com.enchantea.domain.ord.OrderLine;
import com.enchantea.domain.prc.Charge;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PricingCalculator {
    private final PricingConfigLoader CONFIG_LOADER;

    public PricingCalculator(PricingConfigLoader CONFIG_LOADER) {
        this.CONFIG_LOADER = CONFIG_LOADER;
    }

    public Charge computeCharge(List<OrderLine> lines) {
        PricingConfig config = CONFIG_LOADER.get();

        double subtotal = 0;
        for (OrderLine line : lines) {
            subtotal += line.total();
        }
        double surcharge = subtotal < config.smallOrderThreshold() ? config.smallOrderSurcharge() : 0;

        return new Charge(subtotal, config.deliveryFee(), config.serviceFee(), surcharge);
    }
}
