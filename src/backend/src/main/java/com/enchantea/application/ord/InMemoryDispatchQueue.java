package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.DispatchQueue;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderQueue;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class InMemoryDispatchQueue implements DispatchQueue {
    private final Map<String, OrderQueue> queues = new HashMap<>();

    @Override
    public synchronized void add(String restaurantId, Order order) {
        queues.computeIfAbsent(restaurantId, id -> new OrderQueue()).add(order);
    }

    @Override
    public synchronized boolean removeFromDispatch(String restaurantId, String orderId) {
        OrderQueue queue = queues.get(restaurantId);
        return queue != null && queue.remove(orderId);
    }
}
