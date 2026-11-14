package com.enchantea.domain.prc;

import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;

public final class ChargeRecording extends LedgerRecording {

    @Override
    protected void checkEligible(Order order) {
        if (order.status() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Only a delivered order can be charged");
        }
        if (order.charge() == null) {
            throw new IllegalStateException("Order has no charge to record");
        }
    }

    @Override
    protected double amount(Order order) {
        return order.charge().total();
    }

    @Override
    protected LedgerEntryType type() {
        return LedgerEntryType.CHARGE;
    }
}