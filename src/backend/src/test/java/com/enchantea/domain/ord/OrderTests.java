package com.enchantea.domain.ord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class OrderTests {
    private Order newOrder() {
        return Order.create(1L, 2L, List.of(new OrderLine("bubble-tea", 2, 5.0)));
    }

    @Test
    void followsTheFullLifecycleInOrder() {
        Order order = newOrder();

        assertEquals(OrderStatus.PLACED, order.status());
        assertEquals(OrderStatus.ACCEPTED, order.accept());
        assertEquals(OrderStatus.PREPARING, order.prepare());
        assertEquals(OrderStatus.READY, order.markReady());
        assertEquals(OrderStatus.PICKED_UP, order.pickUp(3L));
        assertEquals(OrderStatus.DELIVERED, order.deliver());
    }

    @Test
    void rejectsSkippingAStep() {
        Order order = newOrder(); // still PLACED

        // prepare() is only legal from ACCEPTED, not PLACED
        assertThrows(InvalidOrderTransitionException.class, order::prepare);
    }

    @Test
    void rejectsActingOnAnOrderThatIsAlreadyClosed() {
        Order order = newOrder();
        order.reject("Out of ingredients");

        assertThrows(InvalidOrderTransitionException.class, order::accept);
    }

    @Test
    void rejectingAnOrderRequiresANonBlankReason() {
        Order order = newOrder();

        assertThrows(IllegalArgumentException.class, () -> order.reject(" "));
    }

    @Test
    void customerCanOnlyCancelBeforeTheRestaurantRespondsToIt() {
        Order order = newOrder();
        order.accept(); // now ACCEPTED

        // cancelByCustomer() is only legal from PLACED
        assertThrows(InvalidOrderTransitionException.class, order::cancelByCustomer);
    }

    @Test
    void operatorCanCancelOnceTheRestaurantHasAcceptedIt() {
        Order order = newOrder();
        order.accept();

        assertEquals(OrderStatus.CANCELLED, order.cancelByOperator());
    }

    @Test
    void refundIsOnlyLegalAfterDeliveryOrCancellation() {
        Order order = newOrder();
        order.accept(); // ACCEPTED, not yet delivered or cancelled

        assertThrows(InvalidOrderTransitionException.class, order::refund);
    }

    @Test
    void anOrderMustHaveAtLeastOneLine() {
        assertThrows(IllegalArgumentException.class,
                () -> Order.create(1L, 2L, List.of()));
    }
}
