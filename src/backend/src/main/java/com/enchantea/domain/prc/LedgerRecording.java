package com.enchantea.domain.prc;

import com.enchantea.domain.ord.Order;

// going to be making the template design pattern , every entry goes through the same steps
public abstract class LedgerRecording {
    private final Ledger LEDGER = new Ledger();

    // template design pattern here, final so subclasses cant change the order of the steps.
    public final LedgerEntry record(Order order) {
        checkEligible(order);
        double amount = amount(order);
        return LEDGER.record(order.id(), type(), amount);
    }

    protected abstract void checkEligible(Order order);

    protected abstract double amount(Order order);

    protected abstract LedgerEntryType type();
}