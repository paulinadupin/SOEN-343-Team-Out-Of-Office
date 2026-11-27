package com.enchantea.ui.reg;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.enchantea.application.reg.AccountResponse;
import com.enchantea.application.reg.CourierResponse;
import com.enchantea.application.reg.AccountFacade;
import com.enchantea.application.reg.LoginRequest;
import com.enchantea.application.reg.RegisterRequest;
import com.enchantea.application.reg.RestaurantResponse;
import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.RestaurantStatus;
import com.enchantea.domain.reg.User;

import com.enchantea.application.dash.MapResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountFacade ACCOUNT_FACADE;

    public AuthController(AccountFacade ACCOUNT_FACADE) {
        this.ACCOUNT_FACADE = ACCOUNT_FACADE;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse register(@Valid @RequestBody RegisterRequest request) {
        return ACCOUNT_FACADE.register(request);
    }

    @PostMapping("/login")
    public AccountResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        User user = ACCOUNT_FACADE.authenticate(request);

        // Keep the logged-in user's id and type in the browser's session.
        HttpSession session = servletRequest.getSession();
        servletRequest.changeSessionId();
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("accountType", user.getAccountType().name());
        return AccountResponse.from(user);
    }

    @GetMapping("/me")
    public AccountResponse currentUser(HttpServletRequest servletRequest) {
        HttpSession session = servletRequest.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        Object userId = session.getAttribute("userId");
        if (!(userId instanceof Long)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        return AccountResponse.from(ACCOUNT_FACADE.findById((Long) userId));
    }

    @GetMapping("/restaurant/me")
    public RestaurantResponse currentRestaurant(HttpServletRequest servletRequest) {
        HttpSession session = servletRequest.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        Object userId = session.getAttribute("userId");
        if (!(userId instanceof Long)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        return ACCOUNT_FACADE.findRestaurantById((Long) userId);
    }

    @GetMapping("/restaurants")
    public List<RestaurantResponse> restaurants() {
        return ACCOUNT_FACADE.findRestaurants();
    }

    @PatchMapping("/restaurants/{restaurantId}/status")
    public RestaurantResponse updateRestaurantStatus(
            @PathVariable Long restaurantId,
            @RequestBody RestaurantStatusRequest request,
            HttpServletRequest servletRequest) {
        User user = ACCOUNT_FACADE.requireLoggedIn(servletRequest.getSession(false));
        if (user.getAccountType() != AccountType.OPERATOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operator account required");
        }
        if (request.status() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant status is required");
        }
        return ACCOUNT_FACADE.updateRestaurantStatus(restaurantId, request.status());
    }

    @GetMapping("/on-duty-couriers")
    public List<CourierResponse> onDutyCouriers(HttpServletRequest servletRequest) {
        return ACCOUNT_FACADE.findOnDutyCouriers();
    }

    @GetMapping("/map")
    public MapResponse map(HttpServletRequest servletRequest) {
        ACCOUNT_FACADE.requireLoggedIn(servletRequest.getSession());
        return ACCOUNT_FACADE.findMapData();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest servletRequest) {
        HttpSession session = servletRequest.getSession(false);
        ACCOUNT_FACADE.requireLoggedIn(session);
        session.invalidate();
    }

    public record RestaurantStatusRequest(RestaurantStatus status) { }
}
