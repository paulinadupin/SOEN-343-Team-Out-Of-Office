package com.enchantea.application.ord.contracts;
import com.enchantea.domain.ord.Order;
import java.util.Optional;
public interface PreparationQueue {
    void enqueue(Long restaurantId, Order order);
    Optional<Order> peek(Long restaurantId);
    boolean remove(Long restaurantId, String orderId);
    int size(Long restaurantId);
    void clear();
}
