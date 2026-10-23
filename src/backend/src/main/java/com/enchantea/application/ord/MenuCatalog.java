package com.enchantea.application.ord;

import com.enchantea.domain.ord.MenuItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MenuCatalog {
    private final Map<String, List<MenuItem>> menus = new HashMap<>();

    public synchronized void setMenu(String restaurantId, List<MenuItem> items) {
        menus.put(restaurantId, List.copyOf(items));
    }

    public synchronized List<MenuItem> menu(String restaurantId) {
        return menus.getOrDefault(restaurantId, List.of());
    }

    public synchronized Optional<MenuItem> find(String restaurantId, String itemId) {
        return menu(restaurantId).stream()
                .filter(item -> item.id().equals(itemId))
                .findFirst();
    }
}
