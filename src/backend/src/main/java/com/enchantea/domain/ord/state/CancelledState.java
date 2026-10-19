package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.ord.OrderState;

// only allows the refund action
public final class CancelledState extends AbstractOrderState {
    @Override
    public OrderStatus status() { return OrderStatus.CANCELLED; }

    @Override
    public OrderState refund() { return new RefundedState(); }
}
