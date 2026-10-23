package com.enchantea.application.ord;

import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CurrentUser;

public abstract class CommandCheck {
    private CommandCheck next;

    public CommandCheck then(CommandCheck next) {
        this.next = next;
        return next;
    }

    public void check(CommandType type, CurrentUser user, Object command) {
        verify(type, user, command);
        if (next != null) {
            next.check(type, user, command);
        }
    }

    protected abstract void verify(CommandType type, CurrentUser user, Object command);
}
