package com.enchantea.application.ord.contracts;
/** Person B's permission and world-precondition Chain of Responsibility entry point. */
public interface CommandValidator {
    void validate(CommandType type, CurrentUser user, Object command);
}
