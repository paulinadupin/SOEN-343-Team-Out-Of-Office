package com.enchantea.foundation.prc;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistedLedgerEntryRepository extends JpaRepository<PersistedLedgerEntry, String> {
    List<PersistedLedgerEntry> findByOrderId(String orderId);
}
