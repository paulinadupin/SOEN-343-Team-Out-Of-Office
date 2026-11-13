package com.enchantea.domain.prc;

import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import java.util.Objects;

public final class RefundRecording extends LedgerRecording {
    private final Long OPERATOR_ID;

    public RefundRecording(Long operatorId) {
        this.OPERATOR_ID = Objects.requireNonNull(operatorId, "operatorId");
    }

    @Override
    protected void checkEligible(Order order) {
        if (order.status() != OrderStatus.REFUNDED) {
            throw new IllegalStateException("Only a refunded order can have a refund recorded");
        }
        if (order.charge() == null) {
            throw new IllegalStateException("Order has no charge to refund");
        }
    }

    @Override
    protected double amount(Order order) {
        return order.charge().total();
    }

    @Override
    protected LedgerEntryType type() {
        return LedgerEntryType.REFUND;
    }

    @Override
    protected Long operatorId() {
        return OPERATOR_ID;
    }
}