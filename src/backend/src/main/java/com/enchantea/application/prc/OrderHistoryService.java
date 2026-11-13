package com.enchantea.application.prc;

import com.enchantea.application.ord.OrderRepository;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.prc.LedgerEntry;
import com.enchantea.domain.prc.LedgerEntryType;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderHistoryService {
    private static final Set<OrderStatus> CLOSED_STATUSES = EnumSet.of(
            OrderStatus.DELIVERED, OrderStatus.REFUNDED, OrderStatus.REJECTED, OrderStatus.CANCELLED);

    private final OrderRepository ORDERS;
    private final LedgerRepository LEDGER;
    private final OrderSummaryService SUMMARIES;

    public OrderHistoryService(OrderRepository ORDERS, LedgerRepository LEDGER, OrderSummaryService SUMMARIES) {
        this.ORDERS = ORDERS;
        this.LEDGER = LEDGER;
        this.SUMMARIES = SUMMARIES;
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryEntry> historyFor(CurrentUser user) {
        return ordersFor(user).stream()
                .filter(order -> CLOSED_STATUSES.contains(order.status()))
                .map(this::toEntry)
                .sorted(Comparator.comparing(
                        (OrderHistoryEntry entry) -> entry.summary().closedAt(),
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private List<Order> ordersFor(CurrentUser user) {
        return switch (user.role()) {
            case CUSTOMER -> ORDERS.findByCustomerId(user.id());
            case RESTAURANT -> ORDERS.findByRestaurantId(user.restaurantId());
            case COURIER -> ORDERS.findByCourierId(user.id());
            case OPERATOR -> ORDERS.findAll();
        };
    }

    private OrderHistoryEntry toEntry(Order order) {
        List<LedgerEntry> entries = LEDGER.findByOrderId(order.id());
        double charged = total(entries, LedgerEntryType.CHARGE);
        double refunded = total(entries, LedgerEntryType.REFUND);
        return new OrderHistoryEntry(SUMMARIES.build(order), entries, charged, refunded, charged - refunded);
    }

    private double total(List<LedgerEntry> entries, LedgerEntryType type) {
        return entries.stream()
                .filter(entry -> entry.type() == type)
                .mapToDouble(LedgerEntry::amount)
                .sum();
    }
}