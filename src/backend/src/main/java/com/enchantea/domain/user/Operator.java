package com.enchantea.domain.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OPERATOR")
public class Operator extends User {

    protected Operator() {
    }

    public Operator(String username, String passwordHash) {
        super(username, passwordHash);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.OPERATOR;
    }
}
