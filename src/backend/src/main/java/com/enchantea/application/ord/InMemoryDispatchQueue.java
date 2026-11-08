package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.DispatchQueue;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderQueue;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class InMemoryDispatchQueue implements DispatchQueue {
    private final OrderQueue QUEUE = new OrderQueue();

    @Override
    public synchronized void add(Order order) {
        QUEUE.add(order);
    }

    @Override
    public synchronized boolean removeFromDispatch(String orderId) {
        return QUEUE.remove(orderId);
    }

    @Override
    public synchronized Optional<Order> peek() {
        return QUEUE.first();
    }
}
