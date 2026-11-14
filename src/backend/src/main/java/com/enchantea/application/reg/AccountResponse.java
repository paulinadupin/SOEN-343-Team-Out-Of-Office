package com.enchantea.application.reg;

import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.Customer;
import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.Restaurant;
import com.enchantea.domain.reg.User;

public class AccountResponse {
    private Long userId;
    private String username;
    private AccountType accountType;
    private Location location;

    public AccountResponse() {
    }

    public AccountResponse(Long userId, String username, AccountType accountType, Location location) {
        this.userId = userId;
        this.username = username;
        this.accountType = accountType;
        this.location = location;
    }

    // Build a response without exposing the stored password hash.
    public static AccountResponse from(User user) {
        Location location = null;

        if(user instanceof Customer) {
            location = ((Customer) user).getDeliveryAddress();
        }
        else if (user instanceof Restaurant) {
            location = ((Restaurant) user).getPosition();
        }
        else if(user instanceof Courier) {
            location = ((Courier) user).getPosition();
        }

        return new AccountResponse(
                user.getUserId(),
                user.getUsername(),
                user.getAccountType(),
                location);
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

    public Location getLocation() {
        return location;
    }
}
