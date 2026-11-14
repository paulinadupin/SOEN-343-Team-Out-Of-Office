package com.enchantea.application.reg;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.enchantea.application.dash.MapResponse;
import com.enchantea.application.ord.OrderApplicationService;
import com.enchantea.domain.dlv.state.OfflineState;
import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.CourierStatus;
import com.enchantea.domain.reg.Customer;
import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.Operator;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.RestaurantStatus;
import com.enchantea.domain.reg.User;
import com.enchantea.foundation.reg.UserRepository;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Facade for account registration and login.
 * The controller uses this class instead of coordinating account creation,
 * password hashing, database storage, and authentication itself.
 */
@Service
public class AccountFacade {
    private final UserRepository USERS;
    private final PasswordEncoder PASSWORD_ENCODER;
    private final OrderApplicationService ORDER_APPLICATION_SERVICE;

    public AccountFacade(
            UserRepository USERS,
            PasswordEncoder PASSWORD_ENCODER,
            OrderApplicationService ORDER_APPLICATION_SERVICE) {
        this.USERS = USERS;
        this.PASSWORD_ENCODER = PASSWORD_ENCODER;
        this.ORDER_APPLICATION_SERVICE = ORDER_APPLICATION_SERVICE;
    }

    @Transactional
    public AccountResponse register(RegisterRequest request) {
        if (USERS.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered");
        }

        String passwordHash = PASSWORD_ENCODER.encode(request.getPassword());
        User user = createUser(request, passwordHash);

        try {
            return AccountResponse.from(USERS.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            // The database also checks uniqueness in case two registrations happen at once.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered", exception);
        }
    }

    @Transactional(readOnly = true)
    public User authenticate(LoginRequest request) {
        User user = USERS.findByUsername(request.getUsername()).orElse(null);

        if (user == null || !PASSWORD_ENCODER.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return USERS.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in"));
    }

    @Transactional(readOnly = true)
    public RestaurantResponse findRestaurantById(Long userId) {
        User user = findById(userId);
        if (!(user instanceof Restaurant restaurant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Restaurant account required");
        }
        return RestaurantResponse.from(restaurant);

    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> findRestaurants() {
        return USERS.findAll().stream()
                .filter(Restaurant.class::isInstance)
                .map(Restaurant.class::cast)
                .map(restaurant -> RestaurantResponse.from(
                        restaurant,
                        ORDER_APPLICATION_SERVICE.preparationQueueSize(restaurant.getUserId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourierResponse> findOnDutyCouriers() {
        return USERS.findAll().stream()
                .filter(Courier.class::isInstance)
                .map(Courier.class::cast)
                .filter(courier -> courier.getStatus() != CourierStatus.OFFLINE)
                .map(CourierResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional (readOnly = true)
    public MapResponse findMapData() {
        List<RestaurantResponse> restaurants = findRestaurants();
        List<CourierResponse> couriers = findOnDutyCouriers();
        return new MapResponse(restaurants, couriers, null);
    }

    // @Transactional (readOnly = true)
    // public MapResponse findRestaurantMapData(Long restaurantId) {
    //     Restaurant restaurant = (Restaurant)findById(restaurantId);
    //     List<Order> restaurantOrders = ORDERS.findByRestaurantId(restaurantId);
    // }
    
    public User requireLoggedIn(HttpSession session) {
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Object userId = session.getAttribute("userId");
        if (!(userId instanceof Long)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return USERS.findById((Long) userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Authentication required"));
    }

    private User createUser(RegisterRequest request, String passwordHash) {
        String username = request.getUsername();
        AccountType accountType = request.getAccountType();

        switch (accountType) {
            case CUSTOMER:
                return new Customer(
                        username,
                        passwordHash,
                        requireName(request.getName()),
                        requireLocation(request.getDeliveryAddress(), "deliveryAddress"));
            case RESTAURANT:
                return new Restaurant(
                        username,
                        passwordHash,
                        requireName(request.getName()),
                        request.getRestaurantStatus() == null
                                ? RestaurantStatus.CLOSED
                                : request.getRestaurantStatus(),
                        requireLocation(request.getPosition(), "position"));
            case COURIER:
                return new Courier(
                        username,
                        passwordHash,
                        request.getCourierStatus() == null ? CourierStatus.OFFLINE : request.getCourierStatus(),                        requireLocation(request.getPosition(), "position"));
            case OPERATOR:
                return new Operator(username, passwordHash);
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown account type");
        }
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required for this account type");
        }
        return name;
    }

    private Location requireLocation(Location location, String fieldName) {
        if (location == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, fieldName + " is required for this account type");
        }
        return location;
    }
}