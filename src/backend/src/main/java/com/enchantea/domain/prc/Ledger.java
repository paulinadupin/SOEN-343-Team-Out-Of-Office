package com.enchantea.domain.prc;

import java.time.Instant;
import java.util.UUID;

public final class Ledger {
    public LedgerEntry record(String orderId, LedgerEntryType type, double amount) {
        return new LedgerEntry(UUID.randomUUID().toString(), orderId, type, amount, Instant.now());
    }
}
