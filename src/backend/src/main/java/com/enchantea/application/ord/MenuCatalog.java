package com.enchantea.application.ord;

import com.enchantea.domain.ord.MenuItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MenuCatalog {
    private final Map<Long, List<MenuItem>> MENUS = new HashMap<>();

    public synchronized void setMenu(Long restaurantId, List<MenuItem> items) {
        MENUS.put(restaurantId, List.copyOf(items));
    }

    public synchronized List<MenuItem> menu(Long restaurantId) {
        return MENUS.getOrDefault(restaurantId, List.of());
    }

    public synchronized Optional<MenuItem> find(Long restaurantId, String itemId) {
        return menu(restaurantId).stream()
                .filter(item -> item.id().equals(itemId))
                .findFirst();
    }
}
