package com.enchantea.domain.ord;

import java.util.Objects;

public record OrderLine(String itemId, int quantity, double unitPrice) {
    // TODO: use a MenuItem reference when Person B's menu domain type is available.
    //constructor
    public OrderLine {
        Objects.requireNonNull(itemId, "itemId"); // if itemId is null, it throws a NullPointerException with the message "itemId"
        if (itemId.isBlank() || quantity <= 0 || !Double.isFinite(unitPrice) || unitPrice < 0) {
            throw new IllegalArgumentException("Order line requires an item, positive quantity, and valid price");
        }
    }

    //price of OrderLine before fees and taxes
    public double total() {
        return unitPrice * quantity;
    }
}
