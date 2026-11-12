package com.enchantea.foundation.prc;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistedLedgerEntryRepository extends JpaRepository<PersistedLedgerEntry, String> {
}
