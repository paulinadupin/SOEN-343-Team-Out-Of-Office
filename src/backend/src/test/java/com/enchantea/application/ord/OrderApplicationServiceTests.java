package com.enchantea.application.ord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.prc.Charge;
import com.enchantea.domain.reg.AccountType;
import com.enchantea.foundation.reg.UserRepository;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:ord-service-tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
// Reloads the Spring context (and the in-memory queues/history, which are
// singleton beans)
// between tests, so orders left queued by one test can't affect another.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderApplicationServiceTests {
    @Autowired
    private OrderApplicationService service;

    @Autowired
    private OrderHistoryRecorder history;

    @Autowired
    private UserRepository users;

    private Long restaurantId;
    private CurrentUser customer;
    private CurrentUser restaurantUser;
    private CurrentUser operator;

    @BeforeEach
    void setUp() {
        // "restaurant" is one of the seeded demo accounts, OPEN with a real menu in
        // city.json.
        restaurantId = users.findByUsername("restaurant").orElseThrow().getUserId();
        customer = new CurrentUser(1L, AccountType.CUSTOMER, null);
        restaurantUser = new CurrentUser(2L, AccountType.RESTAURANT, restaurantId);
        operator = new CurrentUser(3L, AccountType.OPERATOR, null);
    }

    private Order place() {
        OrderCommands.PlaceOrder command = new OrderCommands.PlaceOrder(
                restaurantId, List.of(new OrderCommands.RequestedLine("classic-milk-tea", 1)));
        return service.place(customer, command);
    }

    // A quote uses the restaurant's menu price and includes the configured fees and small order surcharge
    @Test
    void quoteUsesRestaurantMenuPricesAndIncludesCurrentFeesAndSurcharge() {
        OrderCommands.PlaceOrder command = new OrderCommands.PlaceOrder(
                restaurantId, List.of(new OrderCommands.RequestedLine("classic-milk-tea", 1)));

        Charge charge = service.quote(customer, command);

        assertEquals(4.99, charge.subtotal(), 0.0001);
        assertEquals(2.99, charge.deliveryFee(), 0.0001);
        assertEquals(1.50, charge.serviceFee(), 0.0001);
        assertEquals(2.00, charge.smallOrderSurcharge(), 0.0001);
        assertEquals(11.48, charge.total(), 0.0001);
    }

    // Only an allowed user can get a quote for an order.
    @Test
    void quoteRejectsAUserWhoCannotPlaceOrders() {
        OrderCommands.PlaceOrder command = new OrderCommands.PlaceOrder(
                restaurantId, List.of(new OrderCommands.RequestedLine("classic-milk-tea", 1)));

        assertThrows(CommandRejectedException.class, () -> service.quote(restaurantUser, command));
    }

    // Placement stores the menu-priced lines and attaches the same calculated charge to the new order
    @Test
    void placingOrderAttachesTheCalculatedChargeAndPricedLines() {
        Order order = place();

        assertEquals(1, order.lines().size());
        assertEquals("classic-milk-tea", order.lines().get(0).itemId());
        assertEquals(1, order.lines().get(0).quantity());
        assertEquals(4.99, order.lines().get(0).unitPrice(), 0.0001);
        assertNotNull(order.charge());
        assertEquals(4.99, order.charge().subtotal(), 0.0001);
        assertEquals(11.48, order.charge().total(), 0.0001);
    }

    // Placement must save the order and publish its initial transition, in addition to calculating its charge.
    @Test
    void placingOrderPersistsItAndPublishesThePlacedEvent() {
        Order order = place();

        assertEquals(order.id(), service.get(order.id()).id());
        assertTrue(service.allOrders().stream().anyMatch(saved -> saved.id().equals(order.id())));
        assertTrue(service.ordersForRestaurant(restaurantId).stream()
                .anyMatch(saved -> saved.id().equals(order.id())));

        List<OrderEvent> events = history.historyFor(order.id());
        assertEquals(1, events.size());
        assertEquals(order.id(), events.get(0).orderId());
        assertEquals(restaurantId, events.get(0).restaurantId());
        assertEquals(customer.id(), events.get(0).customerId());
        assertNull(events.get(0).previousStatus());
        assertEquals(OrderStatus.PLACED, events.get(0).newStatus());
        assertNotNull(events.get(0).occurredAt());
    }

    // Invalid placement is rejected before an order or placement event is created.
    @Test
    void placingOrderRejectsAUserWhoCannotPlaceOrders() {
        OrderCommands.PlaceOrder command = new OrderCommands.PlaceOrder(
                restaurantId, List.of(new OrderCommands.RequestedLine("classic-milk-tea", 1)));
        int orderCountBefore = service.allOrders().size();

        assertThrows(CommandRejectedException.class, () -> service.place(restaurantUser, command));
        assertEquals(orderCountBefore, service.allOrders().size());
        assertTrue(history.all().isEmpty());
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

    // The rejection reason is part of the emitted event as well as the rejected order's state.
    @Test
    void rejectingOrderKeepsTheReasonOnTheOrderAndItsEvent() {
        Order order = place();
        String reason = "The restaurant is closing early";

        Order rejected = service.reject(restaurantUser, new OrderCommands.OrderAction(order.id(), reason));

        assertEquals(OrderStatus.REJECTED, rejected.status());
        assertEquals(reason, rejected.rejectionReason());
        OrderEvent event = history.historyFor(order.id()).get(1);
        assertEquals(OrderStatus.PLACED, event.previousStatus());
        assertEquals(OrderStatus.REJECTED, event.newStatus());
        assertEquals(reason, event.reason());
    }

    // Looking up an unknown id preserves the service's documented not-found failure.
    @Test
    void getThrowsOrderNotFoundForAnUnknownId() {
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class, () -> service.get("missing-order"));

        assertEquals("Order not found: missing-order", exception.getMessage());
    }
}
