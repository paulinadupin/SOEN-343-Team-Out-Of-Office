package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.PreparationQueue;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import java.util.Comparator;
import org.springframework.stereotype.Service;

/** Restores persisted preparation state when needed and enforces queue order. */
@Service
public class PreparationQueueService {
    private final OrderRepository ORDERS;
    private final PreparationQueue PREPARATION_QUEUE;

    public PreparationQueueService(OrderRepository ORDERS, PreparationQueue PREPARATION_QUEUE) {
        this.ORDERS = ORDERS;
        this.PREPARATION_QUEUE = PREPARATION_QUEUE;
    }

    public int size(Long restaurantId) {
        restoreIfEmpty(restaurantId);
        return PREPARATION_QUEUE.size(restaurantId);
    }

    public void requireFirstOrder(Order order) {
        restoreIfEmpty(order.restaurantId());
        Order firstOrder = PREPARATION_QUEUE.peek(order.restaurantId())
                .orElseThrow(() -> new IllegalStateException("Preparation queue is empty"));
        if (!firstOrder.id().equals(order.id())) {
            throw new IllegalStateException("Order is not first in the preparation queue");
        }
    }

    private void restoreIfEmpty(Long restaurantId) {
        if (PREPARATION_QUEUE.peek(restaurantId).isPresent()) {
            return;
        }

        ORDERS.findByRestaurantId(restaurantId).stream()
                .filter(order -> order.status() == OrderStatus.ACCEPTED
                        || order.status() == OrderStatus.PREPARING)
                .sorted(Comparator.comparing(Order::placedAt))
                .forEach(order -> PREPARATION_QUEUE.enqueue(restaurantId, order));
    }
}
