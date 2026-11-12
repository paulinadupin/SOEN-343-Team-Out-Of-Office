package com.enchantea.application.prc;

import com.enchantea.domain.prc.LedgerEntry;
import com.enchantea.foundation.prc.PersistedLedgerEntry;
import com.enchantea.foundation.prc.PersistedLedgerEntryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLedgerRepository {
    private final PersistedLedgerEntryRepository ENTRIES;

    public JpaLedgerRepository(PersistedLedgerEntryRepository ENTRIES) {
        this.ENTRIES = ENTRIES;
    }

    public LedgerEntry save(LedgerEntry entry) {
        PersistedLedgerEntry persisted = new PersistedLedgerEntry(
                entry.entryId(), entry.orderId(), entry.type(), entry.amount(), entry.createdAt());
        ENTRIES.save(persisted);
        return entry;
    }
}
