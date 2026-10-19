package com.enchantea.application.ord.temp;

import com.enchantea.application.ord.contracts.DispatchQueue;
import com.enchantea.application.ord.contracts.PreparationQueue;
import com.enchantea.domain.ord.Order;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** TEMP in-memory queue adapters. Replace with Person B's restaurant queue implementations. */
@Component
public class TempQueues implements PreparationQueue, DispatchQueue {
    private final Map<String, Deque<Order>> preparationOrders = new HashMap<>();
    private final Map<String, Deque<Order>> dispatchOrders = new HashMap<>();

    @Override
    public void enqueue(String restaurantId, Order order) {
        getQueue(preparationOrders, restaurantId).addLast(order);
    }

    @Override
    public Optional<Order> peek(String restaurantId) {
        Deque<Order> queue = preparationOrders.get(restaurantId);
        return queue == null ? Optional.empty() : Optional.ofNullable(queue.peekFirst());
    }

    @Override
    public boolean remove(String restaurantId, String orderId) {
        return removeFrom(preparationOrders, restaurantId, orderId);
    }

    @Override
    public void add(String restaurantId, Order order) {
        getQueue(dispatchOrders, restaurantId).addLast(order);
    }

    @Override
    public boolean removeFromDispatch(String restaurantId, String orderId) {
        return removeFrom(dispatchOrders, restaurantId, orderId);
    }

    private boolean removeFrom(Map<String, Deque<Order>> queues, String restaurantId, String orderId) {
        Deque<Order> queue = queues.get(restaurantId);
        return queue != null && queue.removeIf(order -> order.id().equals(orderId));
    }

    private Deque<Order> getQueue(Map<String, Deque<Order>> queues, String restaurantId) {
        if (!queues.containsKey(restaurantId)) {
            queues.put(restaurantId, new ArrayDeque<>());
        }
        return queues.get(restaurantId);
    }
}
