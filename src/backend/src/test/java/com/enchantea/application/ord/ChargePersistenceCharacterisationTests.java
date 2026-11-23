package com.enchantea.application.ord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.prc.Charge;
import com.enchantea.domain.reg.AccountType;
import com.enchantea.foundation.reg.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:charge-persistence-tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ChargePersistenceCharacterisationTests {
    @Autowired
    private OrderApplicationService service;

    @Autowired
    private OrderRepository orders;

    @Autowired
    private UserRepository users;

    @Test
    @Transactional
    void chargeSurvivesSaveAndReload() {
        Long restaurantId = users.findByUsername("restaurant").orElseThrow().getUserId();
        CurrentUser customer = new CurrentUser(1L, AccountType.CUSTOMER, null);
        Order placed = service.place(customer, new OrderCommands.PlaceOrder(
                restaurantId, List.of(new OrderCommands.RequestedLine("classic-milk-tea", 1))));

        Charge saved = placed.charge();
        Charge reloaded = orders.findById(placed.id()).orElseThrow().charge();

        assertNotNull(reloaded);
        assertEquals(saved.subtotal(), reloaded.subtotal(), 0.0001);
        assertEquals(saved.deliveryFee(), reloaded.deliveryFee(), 0.0001);
        assertEquals(saved.serviceFee(), reloaded.serviceFee(), 0.0001);
        assertEquals(saved.smallOrderSurcharge(), reloaded.smallOrderSurcharge(), 0.0001);
        assertEquals(saved.total(), reloaded.total(), 0.0001);
    }
}