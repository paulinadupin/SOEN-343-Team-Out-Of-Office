package com.enchantea.application.ord;
import com.enchantea.domain.ord.OrderStatus;
import java.time.Instant;

//the event sent out whenever an order changes state
//it includes an event id, the order, the old and new states, and when it happened
public record OrderEvent(String eventId, String orderId, Long restaurantId, Long customerId,
        OrderStatus previousStatus, OrderStatus newStatus, Instant occurredAt, String reason) { }
