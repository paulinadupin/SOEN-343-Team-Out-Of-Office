package com.enchantea.domain.ord;

//OrderState enforces the State pattern

// all of the functions that change the order state
public interface OrderState {
    OrderStatus status();
    OrderState accept();
    OrderState reject();
    OrderState cancelByCustomer();
    OrderState cancelByOperator();
    OrderState prepare();
    OrderState markReady();
    OrderState pickUp();
    OrderState deliver();
    OrderState refund();

    default OrderState restore() {
    throw new InvalidOrderTransitionException(status(), "restore");}
}
