package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.ord.OrderState;

//no other action allowed
public final class RejectedState extends AbstractOrderState {
    @Override
    public OrderStatus status() { return OrderStatus.REJECTED; }

    @Override
    public OrderState restore() {return new PlacedState();}
}
