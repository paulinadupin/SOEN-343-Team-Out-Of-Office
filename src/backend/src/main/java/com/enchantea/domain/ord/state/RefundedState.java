package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.OrderStatus;

// no other action allowed
public final class RefundedState extends AbstractOrderState {
    @Override
    public OrderStatus status() { return OrderStatus.REFUNDED; }
}
