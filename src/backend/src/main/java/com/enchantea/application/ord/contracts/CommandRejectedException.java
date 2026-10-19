package com.enchantea.application.ord.contracts;
public class CommandRejectedException extends RuntimeException {
    public CommandRejectedException(String message) { super(message); }
}
