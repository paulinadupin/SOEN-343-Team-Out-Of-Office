package com.enchantea.application.promo;

import com.enchantea.domain.promo.PromoCode;
import java.io.IOException;
import java.util.List;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PromoConfigLoader {
    private final JsonMapper JSON_MAPPER;
    private List<PromoCode> codes;

    public PromoConfigLoader(JsonMapper JSON_MAPPER) {
        this.JSON_MAPPER = JSON_MAPPER;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void load() throws IOException {
        PromoCode[] loaded = JSON_MAPPER.readValue(
                new ClassPathResource("promo.json").getInputStream(), PromoCode[].class);
        codes = List.of(loaded);
    }

    public List<PromoCode> get() {
        return codes;
    }
}
