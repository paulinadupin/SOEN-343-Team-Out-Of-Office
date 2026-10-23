package com.enchantea.application.reg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Location;
import com.enchantea.foundation.reg.UserRepository;

@Component
public class DemoAccountSeeder implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoAccountSeeder.class);

    private final AccountFacade ACCOUNT_FACADE;
    private final UserRepository USERS;
    private final String PASSWORD;
    private final String CUSTOMER_USERNAME;
    private final String RESTAURANT_USERNAME;
    private final String COURIER_USERNAME;
    private final String OPERATOR_USERNAME;

    public DemoAccountSeeder(
            AccountFacade ACCOUNT_FACADE,
            UserRepository USERS,
            @Value("${demo.accounts.password}") String PASSWORD,
            @Value("${demo.accounts.customer.username}") String CUSTOMER_USERNAME,
            @Value("${demo.accounts.restaurant.username}") String RESTAURANT_USERNAME,
            @Value("${demo.accounts.courier.username}") String COURIER_USERNAME,
            @Value("${demo.accounts.operator.username}") String OPERATOR_USERNAME) {
        this.ACCOUNT_FACADE = ACCOUNT_FACADE;
        this.USERS = USERS;
        this.PASSWORD = PASSWORD;
        this.CUSTOMER_USERNAME = CUSTOMER_USERNAME;
        this.RESTAURANT_USERNAME = RESTAURANT_USERNAME;
        this.COURIER_USERNAME = COURIER_USERNAME;
        this.OPERATOR_USERNAME = OPERATOR_USERNAME;
    }

    @Override
    public void run(ApplicationArguments args) {
        seed(new RegisterRequest(
                CUSTOMER_USERNAME,
                PASSWORD,
                AccountType.CUSTOMER,
                "Demo Customer",
                new Location(45.5017, -73.5673),
                null));

        seed(new RegisterRequest(
                RESTAURANT_USERNAME,
                PASSWORD,
                AccountType.RESTAURANT,
                "Demo Restaurant",
                null,
                new Location(45.5017, -73.5673)));

        seed(new RegisterRequest(
                COURIER_USERNAME,
                PASSWORD,
                AccountType.COURIER,
                null,
                null,
                new Location(45.5017, -73.5673)));

        seed(new RegisterRequest(
                OPERATOR_USERNAME,
                PASSWORD,
                AccountType.OPERATOR,
                null,
                null,
                null));
    }

    private void seed(RegisterRequest request) {
        if (USERS.existsByUsername(request.getUsername())) {
            LOGGER.info("Demo account '{}' already exists; leaving it unchanged.", request.getUsername());
            return;
        }

        ACCOUNT_FACADE.register(request);
        LOGGER.info("Created demo {} account '{}'.", request.getAccountType(), request.getUsername());
    }
}
