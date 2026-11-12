package com.enchantea.application.ord;
import com.enchantea.domain.ord.Order;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//stores ORDERS in memory while the app is running.
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> ORDERS = new HashMap<>();

    @Override
    //save a new order or update an existing one
    public Order save(Order order) {
        ORDERS.put(order.id(), order);
        return order;
    }

    @Override
    //find one order by its id
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(ORDERS.get(id));
    }

    @Override
    //return every order currently stored
    public List<Order> findAll() {
        return new ArrayList<>(ORDERS.values());
    }

    @Override
    public List<Order> findByRestaurantId(String restaurantId) {
        return findAll().stream()
                .filter(order -> order.restaurantId().equals(restaurantId))
                .toList();
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return findAll().stream()
                .filter(order -> order.customerId().equals(customerId))
                .toList();
    }

    @Override
    public List<Order> findByCourierId(String courierId) {
        return findAll().stream()
                .filter(order -> courierId.equals(order.courierId()))
                .toList();
    }
}
