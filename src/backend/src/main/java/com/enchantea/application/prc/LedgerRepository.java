package com.enchantea.application.prc;

import com.enchantea.domain.prc.LedgerEntry;
import java.util.List;

public interface LedgerRepository {
    LedgerEntry save(LedgerEntry entry);
    List<LedgerEntry> findByOrderId(String orderId);
}