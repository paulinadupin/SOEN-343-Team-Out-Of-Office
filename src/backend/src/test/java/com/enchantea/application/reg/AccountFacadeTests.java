package com.enchantea.application.reg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.Customer;
import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.Operator;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.User;
import com.enchantea.foundation.reg.UserRepository;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:account-tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AccountFacadeTests {
    @Autowired
    private AccountFacade accountFacade;

    @Autowired
    private UserRepository users;

    @BeforeEach
    void clearAccounts() {
        users.deleteAll();
    }

    @Test
    void registersEveryAccountTypeWithSequentialGeneratedIds() {
        AccountResponse customer = accountFacade.register(new RegisterRequest(
                "customer", "password123", AccountType.CUSTOMER, "Casey",
                new Location(45.5, -73.6), null));
        AccountResponse restaurant = accountFacade.register(new RegisterRequest(
                "restaurant", "password123", AccountType.RESTAURANT, "Tea House",
                null, new Location(45.6, -73.7)));
        AccountResponse courier = accountFacade.register(new RegisterRequest(
                "courier", "password123", AccountType.COURIER, null, null, null));
        AccountResponse operator = accountFacade.register(new RegisterRequest(
                "operator", "password123", AccountType.OPERATOR, null, null, null));

        assertTrue(customer.getUserId() < restaurant.getUserId());
        assertTrue(restaurant.getUserId() < courier.getUserId());
        assertTrue(courier.getUserId() < operator.getUserId());
        assertEquals(AccountType.CUSTOMER, customer.getAccountType());
        assertEquals(AccountType.RESTAURANT, restaurant.getAccountType());
        assertEquals(AccountType.COURIER, courier.getAccountType());
        assertEquals(AccountType.OPERATOR, operator.getAccountType());
        assertTrue(users.findById(customer.getUserId()).orElseThrow() instanceof Customer);
        assertTrue(users.findById(restaurant.getUserId()).orElseThrow() instanceof Restaurant);
        assertTrue(users.findById(courier.getUserId()).orElseThrow() instanceof Courier);
        assertTrue(users.findById(operator.getUserId()).orElseThrow() instanceof Operator);
    }

    @Test
    void storesOnlyPasswordHashAndAuthenticatesCorrectPassword() {
        AccountResponse created = accountFacade.register(new RegisterRequest(
                "casey", "password123", AccountType.OPERATOR, null, null, null));
        User stored = users.findById(created.getUserId()).orElseThrow();
        User authenticated = accountFacade.authenticate(new LoginRequest("casey", "password123"));

        assertNotEquals("password123", stored.getPasswordHash());
        assertEquals(stored.getUserId(), authenticated.getUserId());
        assertFalse(stored.getPasswordHash().isBlank());
    }

    @Test
    void rejectsDuplicateUsername() {
        RegisterRequest request = new RegisterRequest(
                "same-name", "password123", AccountType.OPERATOR, null, null, null);
        accountFacade.register(request);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> accountFacade.register(request));

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    void requiresRoleSpecificRegistrationFields() {
        RegisterRequest request = new RegisterRequest(
                "missing-fields", "password123", AccountType.CUSTOMER, null, null, null);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> accountFacade.register(request));

        assertEquals(400, exception.getStatusCode().value());
    }
}
