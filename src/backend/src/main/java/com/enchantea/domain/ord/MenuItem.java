package com.enchantea.domain.ord;

import java.util.Objects;

public final class MenuItem {
    private final String ID;
    private final String NAME;
    private final double PRICE;
    private final MenuItemStatus STATUS;

    public MenuItem(String ID, String NAME, double PRICE, MenuItemStatus STATUS) {
        this.ID = Objects.requireNonNull(ID, "ID");
        this.NAME = Objects.requireNonNull(NAME, "NAME");
        this.STATUS = Objects.requireNonNull(STATUS, "STATUS");
        if (!Double.isFinite(PRICE) || PRICE < 0) {
            throw new IllegalArgumentException("Menu item PRICE must be a valid amount");
        }
        this.PRICE = PRICE;
    }

    public String id() { return ID; }
    public String name() { return NAME; }
    public double price() { return PRICE; }
    public MenuItemStatus status() { return STATUS; }

    public boolean isAvailable() {
        return STATUS == MenuItemStatus.AVAILABLE;
    }
}
