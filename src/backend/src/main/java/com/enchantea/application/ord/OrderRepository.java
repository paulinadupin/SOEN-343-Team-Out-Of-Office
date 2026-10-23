package com.enchantea.application.ord;
import com.enchantea.domain.ord.Order;
import java.util.List;
import java.util.Optional;

//interface to save and look up orders
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
    List<Order> findByRestaurantId(String restaurantId);
}
