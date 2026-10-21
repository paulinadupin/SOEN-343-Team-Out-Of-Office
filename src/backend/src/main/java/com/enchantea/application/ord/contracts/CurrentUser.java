package com.enchantea.application.ord.contracts;

import com.enchantea.domain.reg.AccountType;

public final class CurrentUser {
    private final String id;
    private final AccountType role;
    private final String restaurantId;

    public CurrentUser(String id, AccountType role, String restaurantId) {
        this.id = id;
        this.role = role;
        this.restaurantId = restaurantId;
    }

    public String id() { return id; }
    public AccountType role() { return role; }
    public String restaurantId() { return restaurantId; }
}
