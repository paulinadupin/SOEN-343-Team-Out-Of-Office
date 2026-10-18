package com.enchantea.application.reg;

import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotNull
    private AccountType accountType;

    private String name;
    private Location deliveryAddress;
    private Location position;

    // Spring uses this constructor when it turns the JSON request into a Java object.
    public RegisterRequest() {
    }

    public RegisterRequest(
            String username,
            String password,
            AccountType accountType,
            String name,
            Location deliveryAddress,
            Location position) {
        this.username = username;
        this.password = password;
        this.accountType = accountType;
        this.name = name;
        this.deliveryAddress = deliveryAddress;
        this.position = position;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Location deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Location getPosition() {
        return position;
    }

    public void setPosition(Location position) {
        this.position = position;
    }
}
