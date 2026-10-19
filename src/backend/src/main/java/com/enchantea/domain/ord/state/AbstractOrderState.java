package com.enchantea.domain.ord.state;

import com.enchantea.domain.ord.InvalidOrderTransitionException;
import com.enchantea.domain.ord.OrderState;

// makes every action illegal by default
// each state overrides only the actions it allows
abstract class AbstractOrderState implements OrderState {
    @Override
    public OrderState accept() { return illegal("accept"); }

    @Override
    public OrderState reject() { return illegal("reject"); }

    @Override
    public OrderState cancelByCustomer() { return illegal("cancel as customer"); }

    @Override
    public OrderState cancelByOperator() { return illegal("cancel as operator"); }

    @Override
    public OrderState prepare() { return illegal("prepare"); }

    @Override
    public OrderState markReady() { return illegal("mark ready"); }

    @Override
    public OrderState pickUp() { return illegal("pick up"); }

    @Override
    public OrderState deliver() { return illegal("deliver"); }

    @Override
    public OrderState refund() { return illegal("refund"); }

    private OrderState illegal(String action) {
        throw new InvalidOrderTransitionException(status(), action);
    }
}
