package com.enchantea.application.ord;

import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.ord.StateTransition;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

/** Persists orders and publishes a matching event for each recorded transition. */
@Service
public class OrderEventRecorder {
    private final OrderRepository ORDERS;
    private final OrderEventSubject EVENT_SUBJECT;

    public OrderEventRecorder(OrderRepository ORDERS, OrderEventSubject EVENT_SUBJECT) {
        this.ORDERS = ORDERS;
        this.EVENT_SUBJECT = EVENT_SUBJECT;
    }

    public Order saveAndPublish(Order order, OrderStatus oldStatus, String reason) {
        OrderEvent event = createEvent(order, oldStatus, reason);
        ORDERS.save(order);
        EVENT_SUBJECT.publish(event);
        return order;
    }

    private OrderEvent createEvent(Order order, OrderStatus oldStatus, String reason) {
        String eventId = UUID.randomUUID().toString();
        Instant timestamp = Instant.now();

        StateTransition transition = new StateTransition(
                eventId,
                oldStatus,
                order.status(),
                timestamp);
        order.recordTransition(transition);

        return new OrderEvent(
                eventId,
                order.id(),
                order.restaurantId(),
                order.customerId(),
                oldStatus,
                order.status(),
                timestamp,
                reason);
    }
}
