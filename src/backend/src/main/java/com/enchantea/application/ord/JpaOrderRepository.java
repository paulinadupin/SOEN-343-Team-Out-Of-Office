package com.enchantea.application.ord;

import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderLine;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.prc.Charge;
import com.enchantea.foundation.ord.PersistedCharge;
import com.enchantea.foundation.ord.PersistedOrder;
import com.enchantea.foundation.ord.PersistedOrderLine;
import com.enchantea.foundation.ord.PersistedOrderRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaOrderRepository implements OrderRepository {
    private final PersistedOrderRepository ORDERS;

    public JpaOrderRepository(PersistedOrderRepository ORDERS) {
        this.ORDERS = ORDERS;
    }

    @Override
    public Order save(Order order) {
        ORDERS.save(toEntity(order));
        return order;
    }

    // FIX: the read methods are transactional so the lazy order lines load while the session is open
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(String id) {
        return ORDERS.findById(id).map(JpaOrderRepository::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return ORDERS.findAll().stream().map(JpaOrderRepository::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByRestaurantId(Long restaurantId) {
        return ORDERS.findByRestaurantId(restaurantId).stream()
                .map(JpaOrderRepository::toDomain)
                .toList();
    }

    // added for the operator dashboard (requires findByStatus on PersistedOrderRepository)
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStatus(OrderStatus status) {
        return ORDERS.findByStatus(status).stream()
                .map(JpaOrderRepository::toDomain)
                .toList();
    }

    private static PersistedOrder toEntity(Order order) {
        List<PersistedOrderLine> lines = order.lines().stream()
                .map(line -> new PersistedOrderLine(line.itemId(), line.quantity(), line.unitPrice()))
                .toList();
        PersistedCharge charge = order.charge() == null ? null : new PersistedCharge(
                order.charge().subtotal(), order.charge().deliveryFee(),
                order.charge().serviceFee(), order.charge().smallOrderSurcharge());
        return new PersistedOrder(
                order.id(), order.customerId(), order.restaurantId(), order.placedAt(),
                order.closedAt(), order.deliveredAt(), order.rejectionReason(), order.status(), lines, charge);
    }

    private static Order toDomain(PersistedOrder order) {
        List<OrderLine> lines = order.lines().stream()
                .map(line -> new OrderLine(line.itemId(), line.quantity(), line.unitPrice()))
                .toList();
        Charge charge = order.charge() == null ? null : new Charge(
                order.charge().subtotal(), order.charge().deliveryFee(),
                order.charge().serviceFee(), order.charge().smallOrderSurcharge());
        return Order.rehydrate(
                order.id(), order.customerId(), order.restaurantId(), lines, order.placedAt(),
                order.status(), order.closedAt(), order.deliveredAt(), order.rejectionReason(), charge);
    }
}