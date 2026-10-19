package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.OrderState;
import com.enchantea.domain.ord.OrderStatus;

// allows the deliver action and the cancel order action for operator only
public final class PickedUpState extends AbstractOrderState {
    @Override
    public OrderStatus status() { return OrderStatus.PICKED_UP; }

    @Override
    public OrderState deliver() { return new DeliveredState(); }

    @Override
    public OrderState cancelByOperator() { return new CancelledState(); }
}
