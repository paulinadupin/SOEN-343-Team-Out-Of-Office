package com.enchantea.application.ord.contracts;
import com.enchantea.domain.ord.Order;
public interface DispatchQueue {
    void add(String restaurantId, Order order);
    boolean removeFromDispatch(String restaurantId, String orderId);
}
