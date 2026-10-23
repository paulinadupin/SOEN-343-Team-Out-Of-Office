package com.enchantea.ui.ord;

import com.enchantea.application.ord.MenuCatalog;
import com.enchantea.domain.ord.MenuItemStatus;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
public class MenuController {
    private final MenuCatalog MENU_CATALOG;

    public MenuController(MenuCatalog MENU_CATALOG) {
        this.MENU_CATALOG = MENU_CATALOG;
    }

    @GetMapping("/{restaurantId}/menu")
    public List<MenuItemView> menu(@PathVariable String restaurantId) {
        return MENU_CATALOG.menu(restaurantId).stream()
                .map(item -> new MenuItemView(item.id(), item.name(), item.price(), item.status()))
                .toList();
    }

    public record MenuItemView(String id, String name, double price, MenuItemStatus status) { }
}
