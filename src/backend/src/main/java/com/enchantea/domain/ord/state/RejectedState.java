package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.OrderStatus;

//no other action allowed
public final class RejectedState extends AbstractOrderState {
    @Override
    public OrderStatus status() { return OrderStatus.REJECTED; }

    @Override
public OrderState cancelByOperator() {return new CancelledState();}
}
