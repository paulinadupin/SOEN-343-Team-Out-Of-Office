package com.enchantea.application.ord;

import com.enchantea.application.prc.LedgerRepository;
import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.ord.OrderStatus;
import com.enchantea.domain.prc.ChargeRecording;
import com.enchantea.domain.prc.LedgerEntryType;
import com.enchantea.domain.prc.RefundRecording;
import org.springframework.stereotype.Service;

/** Records the charge and refund ledger entries associated with order transitions. */
@Service
public class OrderLedgerService {
    private final LedgerRepository LEDGER_REPOSITORY;

    public OrderLedgerService(LedgerRepository LEDGER_REPOSITORY) {
        this.LEDGER_REPOSITORY = LEDGER_REPOSITORY;
    }

    public void recordCharge(Order order) {
        LEDGER_REPOSITORY.save(new ChargeRecording().record(order));
    }

    public void refund(Order order, Long userId) {
        if (order.status() != OrderStatus.DELIVERED) {
            throw new CommandRejectedException("Only a delivered order can be refunded");
        }
        boolean charged = LEDGER_REPOSITORY.findByOrderId(order.id()).stream()
                .anyMatch(entry -> entry.type() == LedgerEntryType.CHARGE);
        if (!charged) {
            throw new CommandRejectedException("Order has no charge to refund");
        }

        order.refund();
        LEDGER_REPOSITORY.save(new RefundRecording(userId).record(order));
    }
}
