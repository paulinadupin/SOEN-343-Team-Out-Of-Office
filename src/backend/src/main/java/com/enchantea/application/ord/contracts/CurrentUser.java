package com.enchantea.application.ord.contracts;

import com.enchantea.domain.reg.AccountType;

public final class CurrentUser {
    private final String ID;
    private final AccountType ROLE;
    private final String RESTAURANT_ID;

    public CurrentUser(String ID, AccountType ROLE, String RESTAURANT_ID) {
        this.ID = ID;
        this.ROLE = ROLE;
        this.RESTAURANT_ID = RESTAURANT_ID;
    }

    public String id() { return ID; }
    public AccountType role() { return ROLE; }
    public String restaurantId() { return RESTAURANT_ID; }
}
