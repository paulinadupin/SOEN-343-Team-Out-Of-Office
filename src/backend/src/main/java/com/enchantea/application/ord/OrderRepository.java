package com.enchantea.application.ord;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import java.util.List;
import java.util.Optional;

//interface to save and look up orders
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
    List<Order> findByRestaurantId(Long restaurantId);
    default List<Order> findByStatus(OrderStatus status) {
        return findAll().stream()
                .filter(order -> order.status() == status)
                .toList();
    }
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByCourierId(Long courierId);
}
