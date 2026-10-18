package com.enchantea.application.reg;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.CourierStatus;
import com.enchantea.domain.reg.Customer;
import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.Operator;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.RestaurantStatus;
import com.enchantea.domain.reg.User;
import com.enchantea.infrastructure.persistence.user.UserRepository;

/**
 * Facade for account registration and login.
 * The controller uses this class instead of coordinating account creation,
 * password hashing, database storage, and authentication itself.
 */
@Service
public class AccountFacade {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AccountFacade(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AccountResponse register(RegisterRequest request) {
        if (users.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        User user = createUser(request, passwordHash);

        try {
            return AccountResponse.from(users.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            // The database also checks uniqueness in case two registrations happen at once.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered", exception);
        }
    }

    @Transactional(readOnly = true)
    public User authenticate(LoginRequest request) {
        User user = users.findByUsername(request.getUsername()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return user;
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
                        RestaurantStatus.CLOSED,
                        requireLocation(request.getPosition(), "position"));
            case COURIER:
                return new Courier(username, passwordHash, CourierStatus.OFFLINE, request.getPosition());
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
