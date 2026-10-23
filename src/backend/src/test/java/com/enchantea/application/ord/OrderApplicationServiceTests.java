package com.enchantea.application.ord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.reg.AccountType;
import com.enchantea.foundation.reg.UserRepository;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:ord-service-tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
// Reloads the Spring context (and the in-memory queues/history, which are singleton beans)
// between tests, so orders left queued by one test can't affect another.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderApplicationServiceTests {
    @Autowired
    private OrderApplicationService service;

    @Autowired
    private OrderHistoryRecorder history;

    @Autowired
    private UserRepository users;

    private String restaurantId;
    private CurrentUser customer;
    private CurrentUser restaurantUser;
    private CurrentUser operator;

    @BeforeEach
    void setUp() {
        // "restaurant" is one of the seeded demo accounts, OPEN with a real menu in city.json.
        restaurantId = users.findByUsername("restaurant").orElseThrow().getUserId().toString();
        customer = new CurrentUser("customer-1", AccountType.CUSTOMER, null);
        restaurantUser = new CurrentUser("restaurant-staff-1", AccountType.RESTAURANT, restaurantId);
        operator = new CurrentUser("operator-1", AccountType.OPERATOR, null);
    }

    private Order place() {
        OrderCommands.PlaceOrder command = new OrderCommands.PlaceOrder(
                restaurantId, List.of(new OrderCommands.RequestedLine("classic-milk-tea", 1)));
        return service.place(customer, command);
    }

    @Test
    void onlyTheFirstOrderInTheQueueCanBePrepared() {
        Order first = place();
        Order second = place();
        service.accept(restaurantUser, new OrderCommands.OrderAction(first.id(), null));
        service.accept(restaurantUser, new OrderCommands.OrderAction(second.id(), null));

        // the second order arrived later, so it must wait behind the first
        assertThrows(IllegalStateException.class,
                () -> service.prepare(restaurantUser, new OrderCommands.OrderAction(second.id(), null)));

        Order prepared = service.prepare(restaurantUser, new OrderCommands.OrderAction(first.id(), null));
        assertEquals(OrderStatus.PREPARING, prepared.status());
    }

    @Test
    void cancellingByOperatorRemovesTheOrderFromThePreparationQueue() {
        Order first = place();
        Order second = place();
        service.accept(restaurantUser, new OrderCommands.OrderAction(first.id(), null));
        service.accept(restaurantUser, new OrderCommands.OrderAction(second.id(), null));

        service.cancelByOperator(operator, new OrderCommands.OrderAction(first.id(), null));

        // with the first order gone, the second is now at the front of the queue
        Order prepared = service.prepare(restaurantUser, new OrderCommands.OrderAction(second.id(), null));
        assertEquals(OrderStatus.PREPARING, prepared.status());
    }

    @Test
    void everyTransitionIsRecordedWithItsOwnEventId() {
        Order order = place();
        service.accept(restaurantUser, new OrderCommands.OrderAction(order.id(), null));
        service.prepare(restaurantUser, new OrderCommands.OrderAction(order.id(), null));

        List<OrderEvent> events = history.historyFor(order.id());

        assertEquals(3, events.size()); // placed, accepted, prepared
        for (OrderEvent event : events) {
            assertNotNull(event.eventId());
        }

        Set<String> eventIds = events.stream().map(OrderEvent::eventId).collect(Collectors.toSet());
        assertEquals(events.size(), eventIds.size()); // every event id is unique, not just adjacent ones
    }
}
