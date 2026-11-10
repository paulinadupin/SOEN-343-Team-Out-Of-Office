package com.enchantea.application.ord;

import com.enchantea.domain.ord.MenuItem;
import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.foundation.reg.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class CityConfigLoader {
    private static final String CONFIG_FILE = "city.json";

    private final JsonMapper JSON_MAPPER;
    private final UserRepository USERS;
    private final MenuCatalog MENU_CATALOG;

    public CityConfigLoader(JsonMapper JSON_MAPPER, UserRepository USERS, MenuCatalog MENU_CATALOG) {
        this.JSON_MAPPER = JSON_MAPPER;
        this.USERS = USERS;
        this.MENU_CATALOG = MENU_CATALOG;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void load() {
        for (CityConfig.RestaurantConfig config : readConfig().restaurants()) {
            if (!(USERS.findByUsername(config.username()).orElse(null) instanceof Restaurant restaurant)) {
                continue;
            }

            restaurant.setStatus(config.status());
            restaurant.setPosition(new Location(config.position().lat(), config.position().lng()));
            USERS.save(restaurant);

            List<MenuItem> items = config.menu().stream()
                    .map(item -> new MenuItem(item.id(), item.name(), item.price(), item.status()))
                    .toList();
            MENU_CATALOG.setMenu(restaurant.getUserId(), items);
        }
    }

    private CityConfig readConfig() {
        try (InputStream input = new ClassPathResource(CONFIG_FILE).getInputStream()) {
            return JSON_MAPPER.readValue(input, CityConfig.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + CONFIG_FILE, e);
        }
    }
}
