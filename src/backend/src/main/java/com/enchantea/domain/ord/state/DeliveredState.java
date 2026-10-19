package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.OrderState;
import com.enchantea.domain.ord.OrderStatus;

// allows the refund action and the cancel order action for operator only
public final class DeliveredState extends AbstractOrderState {
    @Override
    public OrderStatus status() { return OrderStatus.DELIVERED; }

    @Override
    public OrderState cancelByOperator() { return new CancelledState(); }

    @Override
    public OrderState refund() { return new RefundedState(); }
}
