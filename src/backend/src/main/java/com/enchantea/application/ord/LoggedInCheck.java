package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CurrentUser;

public class LoggedInCheck extends CommandCheck {
    @Override
    protected void verify(CommandType type, CurrentUser user, Object command) {
        if (user == null || user.id() == null || user.role() == null) {
            throw new CommandRejectedException("A current user is required");
        }
    }
}
