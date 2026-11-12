package com.enchantea.domain.prc;

import java.time.Instant;

public final class LedgerEntry {
    private final String ENTRY_ID;
    private final String ORDER_ID;
    private final LedgerEntryType TYPE;
    private final double AMOUNT;
    private final Instant CREATED_AT;

    LedgerEntry(String entryId, String orderId, LedgerEntryType type, double amount, Instant createdAt) {
        this.ENTRY_ID = entryId;
        this.ORDER_ID = orderId;
        this.TYPE = type;
        this.AMOUNT = amount; 
        this.CREATED_AT = createdAt;
    }

    public static LedgerEntry fromDatabase(String entryId, String orderId, LedgerEntryType type, double amount, Instant createdAt) {
        return new LedgerEntry(entryId, orderId, type, amount, createdAt);
    }

    public String entryId() {
        return ENTRY_ID;
    }

    public String orderId() {
        return ORDER_ID;
    }

    public LedgerEntryType type() {
        return TYPE;
    }

    public double amount() {
        return AMOUNT;
    }

    public Instant createdAt() {
        return CREATED_AT;
    }
}
