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
    private final MenuCatalog menuCatalog;
    private final AccountFacade accountFacade;

    public MenuController(MenuCatalog menuCatalog, AccountFacade accountFacade) {
        this.menuCatalog = menuCatalog;
        this.accountFacade=accountFacade;
    }

    @GetMapping("/{restaurantId}/menu")
    public List<MenuItemView> menu(@PathVariable String restaurantId, HttpServletRequest servletRequest) {
        currentUser(servletRequest);
        return menuCatalog.menu(restaurantId).stream()
                .map(item -> new MenuItemView(item.id(), item.name(), item.price(), item.status()))
                .toList();
    }

    private CurrentUser currentUser(HttpServletRequest servletRequest) {
        User user = accountFacade.requireLoggedIn(servletRequest.getSession(false));
        AccountType accountType = user.getAccountType();
        String restaurantId = user instanceof Restaurant ? user.getUserId().toString() : null;
        return new CurrentUser(user.getUserId().toString(), accountType, restaurantId);
    }

    public record MenuItemView(String id, String name, double price, MenuItemStatus status) { }
}
