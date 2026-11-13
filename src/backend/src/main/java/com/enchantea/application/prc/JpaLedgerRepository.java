package com.enchantea.application.prc;

import com.enchantea.domain.prc.LedgerEntry;
import com.enchantea.foundation.prc.PersistedLedgerEntry;
import com.enchantea.foundation.prc.PersistedLedgerEntryRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLedgerRepository implements LedgerRepository {
    private final PersistedLedgerEntryRepository ENTRIES;

    public JpaLedgerRepository(PersistedLedgerEntryRepository ENTRIES) {
        this.ENTRIES = ENTRIES;
    }

    @Override
    public LedgerEntry save(LedgerEntry entry) {
        PersistedLedgerEntry persisted = new PersistedLedgerEntry(
                entry.entryId(), entry.orderId(), entry.type(), entry.amount(), entry.createdAt(), entry.operatorId());
        ENTRIES.save(persisted);
        return entry;
    }

    @Override
    public List<LedgerEntry> findByOrderId(String orderId) {
        return ENTRIES.findByOrderId(orderId).stream()
                .map(entry -> LedgerEntry.fromDatabase(
                         entry.entryId(), entry.orderId(), entry.type(), entry.amount(), entry.createdAt(), entry.operatorId()))
                .toList();
    }
}