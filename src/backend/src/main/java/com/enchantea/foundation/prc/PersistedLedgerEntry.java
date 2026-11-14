package com.enchantea.foundation.prc;

import com.enchantea.domain.prc.LedgerEntryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ledger_entries") 
public class PersistedLedgerEntry {
    @Id
    private String entryId;
    @Column(nullable = false)
    private String orderId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerEntryType type;
    @Column(nullable = false)
    private double amount;
    @Column(nullable = false)
    private Instant createdAt;
    private Long operatorId;

    protected PersistedLedgerEntry() {
    }

    public PersistedLedgerEntry(String entryId, String orderId, LedgerEntryType type, double amount, Instant createdAt, Long operatorId) {
        this.entryId = entryId;
        this.orderId = orderId;
        this.type = type;
        this.amount = amount;
        this.createdAt = createdAt;
        this.operatorId = operatorId;
    }

    public String entryId() { return entryId; }
    public String orderId() { return orderId; }
    public LedgerEntryType type() { return type; }
    public double amount() { return amount; }
    public Instant createdAt() { return createdAt; }
    public Long operatorId() { return operatorId; }
}
