package com.enchantea.application.prc;

import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.prc.Charge;
import java.time.Instant;
import java.util.List;

//what the customer sees for a delivered or refunded order (R-PRC-03)
public record OrderSummary(
        String orderId,
        String restaurantId,
        String restaurantName,
        List<Line> lines,
        Charge charge,
        Instant placedAt,
        Instant deliveredAt,
        Instant closedAt,
        OrderStatus finalStatus) {

    public record Line(String itemId, String name, int quantity, double unitPrice, double lineTotal) { }
}