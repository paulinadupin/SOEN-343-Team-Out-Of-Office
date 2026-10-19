package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.OrderState;
import com.enchantea.domain.ord.OrderStatus;

// allows the accept action, reject action and the cancel order action for customer only
public final class PlacedState extends AbstractOrderState {
    @Override
    public OrderStatus status() { return OrderStatus.PLACED; }

    @Override
    public OrderState accept() { return new AcceptedState(); }

    @Override
    public OrderState reject() { return new RejectedState(); }

    @Override
    public OrderState cancelByCustomer() { return new CancelledState(); }

}
