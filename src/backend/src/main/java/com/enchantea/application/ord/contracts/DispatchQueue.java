package com.enchantea.application.ord.contracts;
import java.util.Optional;
import com.enchantea.domain.ord.Order;

public interface DispatchQueue {
    void add(Order order);
    Optional<Order> peek();
    boolean removeFromDispatch(String orderId);
    boolean contains(String orderId);
}
