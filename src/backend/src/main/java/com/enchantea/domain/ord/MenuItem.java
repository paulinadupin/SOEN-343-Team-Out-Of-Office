package com.enchantea.domain.ord;

import java.util.Objects;

public final class MenuItem {
    private final String id;
    private final String name;
    private final double price;
    private final MenuItemStatus status;

    public MenuItem(String id, String name, double price, MenuItemStatus status) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.status = Objects.requireNonNull(status, "status");
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Menu item price must be a valid amount");
        }
        this.price = price;
    }

    public String id() { return id; }
    public String name() { return name; }
    public double price() { return price; }
    public MenuItemStatus status() { return status; }

    public boolean isAvailable() {
        return status == MenuItemStatus.AVAILABLE;
    }
}
