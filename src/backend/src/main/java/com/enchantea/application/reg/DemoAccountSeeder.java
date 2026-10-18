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
    private static final Logger logger = LoggerFactory.getLogger(DemoAccountSeeder.class);

    private final AccountFacade accountFacade;
    private final UserRepository users;
    private final String password;
    private final String customerUsername;
    private final String restaurantUsername;
    private final String courierUsername;
    private final String operatorUsername;

    public DemoAccountSeeder(
            AccountFacade accountFacade,
            UserRepository users,
            @Value("${demo.accounts.password}") String password,
            @Value("${demo.accounts.customer.username}") String customerUsername,
            @Value("${demo.accounts.restaurant.username}") String restaurantUsername,
            @Value("${demo.accounts.courier.username}") String courierUsername,
            @Value("${demo.accounts.operator.username}") String operatorUsername) {
        this.accountFacade = accountFacade;
        this.users = users;
        this.password = password;
        this.customerUsername = customerUsername;
        this.restaurantUsername = restaurantUsername;
        this.courierUsername = courierUsername;
        this.operatorUsername = operatorUsername;
    }

    @Override
    public void run(ApplicationArguments args) {
        seed(new RegisterRequest(
                customerUsername,
                password,
                AccountType.CUSTOMER,
                "Demo Customer",
                new Location(45.5017, -73.5673),
                null));

        seed(new RegisterRequest(
                restaurantUsername,
                password,
                AccountType.RESTAURANT,
                "Demo Restaurant",
                null,
                new Location(45.5017, -73.5673)));

        seed(new RegisterRequest(
                courierUsername,
                password,
                AccountType.COURIER,
                null,
                null,
                new Location(45.5017, -73.5673)));

        seed(new RegisterRequest(
                operatorUsername,
                password,
                AccountType.OPERATOR,
                null,
                null,
                null));
    }

    private void seed(RegisterRequest request) {
        if (users.existsByUsername(request.getUsername())) {
            logger.info("Demo account '{}' already exists; leaving it unchanged.", request.getUsername());
            return;
        }

        accountFacade.register(request);
        logger.info("Created demo {} account '{}'.", request.getAccountType(), request.getUsername());
    }
}
