package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.PreparationQueue;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPreparationQueue implements PreparationQueue {
    private final Map<String, OrderQueue> queues = new HashMap<>();

    @Override
    public synchronized void enqueue(String restaurantId, Order order) {
        queueFor(restaurantId).add(order);
    }

    @Override
    public synchronized Optional<Order> peek(String restaurantId) {
        OrderQueue queue = queues.get(restaurantId);
        return queue == null ? Optional.empty() : queue.first();
    }

    @Override
    public synchronized boolean remove(String restaurantId, String orderId) {
        OrderQueue queue = queues.get(restaurantId);
        return queue != null && queue.remove(orderId);
    }

    private OrderQueue queueFor(String restaurantId) {
        return queues.computeIfAbsent(restaurantId, id -> new OrderQueue());
    }
}
