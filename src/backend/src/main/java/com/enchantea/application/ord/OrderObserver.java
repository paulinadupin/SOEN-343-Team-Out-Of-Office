package com.enchantea.application.ord;

//observer design pattern
@FunctionalInterface
public interface OrderObserver {
    void onOrderStateChanged(OrderEvent event);
}
