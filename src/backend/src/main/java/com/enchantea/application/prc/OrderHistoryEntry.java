package com.enchantea.application.prc;

import com.enchantea.domain.prc.LedgerEntry;
import java.util.List;

public record OrderHistoryEntry(
        OrderSummary summary,
        List<LedgerEntry> ledgerEntries,
        double chargedTotal,
        double refundedTotal,
        double netTotal) { }