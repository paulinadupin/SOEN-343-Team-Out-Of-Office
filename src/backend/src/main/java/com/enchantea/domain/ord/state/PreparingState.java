package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.OrderState;
import com.enchantea.domain.ord.OrderStatus;

// allows the mark ready action and the cancel order action for operator only
public final class PreparingState extends AbstractOrderState {
    @Override
    public OrderStatus status() { return OrderStatus.PREPARING; }

    @Override
    public OrderState markReady() { return new ReadyState(); }

    @Override
    public OrderState cancelByOperator() { return new CancelledState(); }
}
