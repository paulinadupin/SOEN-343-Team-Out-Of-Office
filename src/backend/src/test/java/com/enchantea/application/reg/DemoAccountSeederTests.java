package com.enchantea.application.reg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.User;
import com.enchantea.foundation.reg.UserRepository;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:seeder-tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DemoAccountSeederTests {
    @Autowired
    private DemoAccountSeeder seeder;

    @Autowired
    private UserRepository users;

    @BeforeEach
    void clearAccounts() {
        users.deleteAll();
    }

    @Test
    void seedsExactlyOneAccountPerRole() {
        seeder.run(null);

        List<User> seeded = users.findAll();
        Set<AccountType> roles = seeded.stream().map(User::getAccountType).collect(Collectors.toSet());

        assertEquals(4, seeded.size());
        assertEquals(
                Set.of(AccountType.CUSTOMER, AccountType.RESTAURANT, AccountType.COURIER, AccountType.OPERATOR),
                roles);
    }

    @Test
    void runningTwiceDoesNotCreateDuplicates() {
        seeder.run(null);
        seeder.run(null);

        assertEquals(4, users.findAll().size());
    }
}
