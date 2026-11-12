package com.enchantea.application.dash;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import com.enchantea.application.reg.AccountFacade;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.User;

import jakarta.servlet.http.HttpServletRequest;

public class MapController {
    private final MapService MAP_SERVICE;
    private final AccountFacade ACCOUNT_FACADE;

    public MapController(MapService mapService, AccountFacade accountFacade) {
        this.MAP_SERVICE = mapService;
        this.ACCOUNT_FACADE = accountFacade;
    }

    @GetMapping("/restaurant")
    public MapResponse restaurantMap(HttpServletRequest servletRequest) {
        User user = ACCOUNT_FACADE.requireLoggedIn(
        servletRequest.getSession(false)
    );

    if (!(user instanceof Restaurant restaurant)) {
        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "Restaurant account required"
        );
    }

    return MAP_SERVICE.restaurantMap(restaurant.getUserId());
    }
}
