package com.enchantea.domain.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class LedgerTests {

    @Test
    void recordsAnEntryWithTheGivenOrderIdTypeAndAmount() {
        Ledger ledger = new Ledger();

        LedgerEntry entry = ledger.record("order-1", LedgerEntryType.CHARGE, 21.46);

        assertEquals("order-1", entry.orderId());
        assertEquals(LedgerEntryType.CHARGE, entry.type());
        assertEquals(21.46, entry.amount(), 0.0001);
        assertNotNull(entry.createdAt());
    }

    @Test
    void everyEntryGetsAUniqueId() {
        Ledger ledger = new Ledger();

        LedgerEntry first = ledger.record("order-1", LedgerEntryType.CHARGE, 21.46);
        LedgerEntry second = ledger.record("order-1", LedgerEntryType.CHARGE, 21.46);

        assertNotEquals(first.entryId(), second.entryId());
    }
}