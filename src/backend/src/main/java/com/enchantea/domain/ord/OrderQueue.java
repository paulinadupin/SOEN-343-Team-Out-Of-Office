package com.enchantea.domain.ord;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public final class OrderQueue {
    private final Deque<Order> orders = new ArrayDeque<>();

    public void add(Order order) {
        if (contains(order.id())) {
            throw new IllegalStateException("Order " + order.id() + " is already in the queue");
        }
        orders.addLast(order);
    }

    public Optional<Order> first() {
        return Optional.ofNullable(orders.peekFirst());
    }

    public boolean remove(String orderId) {
        return orders.removeIf(order -> order.id().equals(orderId));
    }

    public boolean contains(String orderId) {
        return orders.stream().anyMatch(order -> order.id().equals(orderId));
    }

    public List<Order> orders() {
        return List.copyOf(orders);
    }
}
