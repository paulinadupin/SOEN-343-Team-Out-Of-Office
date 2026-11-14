package com.enchantea.domain.ord;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public final class OrderQueue {
    private final Deque<Order> ORDERS = new ArrayDeque<>();

    public void add(Order order) {
        if (contains(order.id())) {
            throw new IllegalStateException("Order " + order.id() + " is already in the queue");
        }
        ORDERS.addLast(order);
    }

    public Optional<Order> first() {
        return Optional.ofNullable(ORDERS.peekFirst());
    }

    public boolean remove(String orderId) {
        return ORDERS.removeIf(order -> order.id().equals(orderId));
    }

    public boolean contains(String orderId) {
        return ORDERS.stream().anyMatch(order -> order.id().equals(orderId));
    }

    public List<Order> orders() {
        return List.copyOf(ORDERS);
    }

    public int size(){
        return ORDERS.size();
    }

    public void clear() {
        ORDERS.clear();
    }

}
