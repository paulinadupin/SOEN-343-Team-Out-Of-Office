package com.enchantea.application.ord;
import com.enchantea.domain.ord.Order;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
//stores orders in memory while the app is running.
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> orders = new HashMap<>();

    @Override
    //save a new order or update an existing one
    public Order save(Order order) {
        orders.put(order.id(), order);
        return order;
    }

    @Override
    //find one order by its ID
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    //return every order currently stored
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
}
