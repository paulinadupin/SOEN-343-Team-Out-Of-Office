package com.enchantea.application.reg;

import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.User;

public class AccountResponse {
    private Long userId;
    private String username;
    private AccountType accountType;

    public AccountResponse() {
    }

    public AccountResponse(Long userId, String username, AccountType accountType) {
        this.userId = userId;
        this.username = username;
        this.accountType = accountType;
    }

    // Build a response without exposing the stored password hash.
    public static AccountResponse from(User user) {
        return new AccountResponse(user.getUserId(), user.getUsername(), user.getAccountType());
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}
