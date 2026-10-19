package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.OrderState;
import com.enchantea.domain.ord.OrderStatus;

// allows the pick up action and the cancel order action for operator only
public final class ReadyState extends AbstractOrderState {
    @Override
    public OrderStatus status() { return OrderStatus.READY; }

    @Override
    public OrderState pickUp() { return new PickedUpState(); }

    @Override
    public OrderState cancelByOperator() { return new CancelledState(); }
}
