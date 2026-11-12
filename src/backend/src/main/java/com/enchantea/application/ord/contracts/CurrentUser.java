package com.enchantea.application.ord.contracts;

import com.enchantea.domain.reg.AccountType;

public final class CurrentUser {
    private final Long ID;
    private final AccountType ROLE;
    private final Long RESTAURANT_ID;

    public CurrentUser(Long ID, AccountType ROLE, Long RESTAURANT_ID) {
        this.ID = ID;
        this.ROLE = ROLE;
        this.RESTAURANT_ID = RESTAURANT_ID;
    }

    public Long id() { return ID; }
    public AccountType role() { return ROLE; }
    public Long restaurantId() { return RESTAURANT_ID; }
}
