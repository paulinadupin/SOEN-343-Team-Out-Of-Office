package com.enchantea.application.reg;

import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Customer;
import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.User;

public class AccountResponse {
    private Long userId;
    private String username;
    private AccountType accountType;
    private Location deliveryAddress;

    public AccountResponse() {
    }

    public AccountResponse(Long userId, String username, AccountType accountType, Location deliveryAddress) {
        this.userId = userId;
        this.username = username;
        this.accountType = accountType;
        this.deliveryAddress = deliveryAddress;
    }

    // Build a response without exposing the stored password hash.
    public static AccountResponse from(User user) {
        Location deliveryAddress = user instanceof Customer customer
                ? customer.getDeliveryAddress()
                : null;
        return new AccountResponse(
                user.getUserId(),
                user.getUsername(),
                user.getAccountType(),
                deliveryAddress);
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

    public Location getDeliveryAddress() {
        return deliveryAddress;
    }
}
