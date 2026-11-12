package com.enchantea.application.prc;

import java.io.IOException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PricingConfigLoader {
    private final JsonMapper JSON_MAPPER;
    private PricingConfig config;

    public PricingConfigLoader(JsonMapper JSON_MAPPER) {
        this.JSON_MAPPER = JSON_MAPPER;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void load() throws IOException {
        config = JSON_MAPPER.readValue(new ClassPathResource("pricing.json").getInputStream(), PricingConfig.class);
    }

    public PricingConfig get() {
        return config;
    }
}
