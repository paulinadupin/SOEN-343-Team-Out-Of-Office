package com.enchantea.foundation.ord;

import jakarta.persistence.Embeddable;

@Embeddable
public class PersistedOrderLine {
    private String itemId;
    private int quantity;
    private double unitPrice;

    protected PersistedOrderLine() {
    }

    public PersistedOrderLine(String itemId, int quantity, double unitPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String itemId() { return itemId; }
    public int quantity() { return quantity; }
    public double unitPrice() { return unitPrice; }
}
