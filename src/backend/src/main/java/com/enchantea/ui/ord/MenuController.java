package com.enchantea.ui.ord;

import com.enchantea.application.ord.MenuCatalog;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.reg.AccountFacade;
import com.enchantea.domain.ord.MenuItemStatus;
import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
public class MenuController {
    private final MenuCatalog MENU_CATALOG;
    private final AccountFacade ACCOUNT_FACADE;

    public MenuController(MenuCatalog MENU_CATALOG, AccountFacade ACCOUNT_FACADE) {
        this.MENU_CATALOG = MENU_CATALOG;
        this.ACCOUNT_FACADE = ACCOUNT_FACADE;
    }

    @GetMapping("/{restaurantId}/menu")
    public List<MenuItemView> menu(@PathVariable Long restaurantId, HttpServletRequest servletRequest) {
        currentUser(servletRequest);
        return MENU_CATALOG.menu(restaurantId).stream()
                .map(item -> new MenuItemView(item.id(), item.name(), item.price(), item.status()))
                .toList();
    }

    private CurrentUser currentUser(HttpServletRequest servletRequest) {
        User user = ACCOUNT_FACADE.requireLoggedIn(servletRequest.getSession(false));
        AccountType accountType = user.getAccountType();
        Long restaurantId = user instanceof Restaurant ? user.getUserId() : null;
        return new CurrentUser(user.getUserId(), accountType, restaurantId);
    }

    public record MenuItemView(String id, String name, double price, MenuItemStatus status) { }
}
