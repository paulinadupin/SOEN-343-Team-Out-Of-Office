package com.enchantea.application.ord.contracts;
import com.enchantea.domain.ord.Order;
import java.util.Optional;
public interface PreparationQueue {
    void enqueue(String restaurantId, Order order);
    Optional<Order> peek(String restaurantId);
    boolean remove(String restaurantId, String orderId);
}
