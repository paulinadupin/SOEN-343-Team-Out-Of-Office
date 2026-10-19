package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.OrderState;
import com.enchantea.domain.ord.OrderStatus;

//allows the prepare action and the cancel order action for operator only
public final class AcceptedState extends AbstractOrderState {
    @Override
    public OrderStatus status() { return OrderStatus.ACCEPTED; }

    @Override
    public OrderState prepare() { return new PreparingState(); }

    @Override
    public OrderState cancelByOperator() { return new CancelledState(); }
}
