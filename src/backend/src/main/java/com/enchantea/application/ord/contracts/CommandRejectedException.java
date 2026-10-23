package com.enchantea.application.ord.contracts;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommandRejectedException extends RuntimeException {
    public CommandRejectedException(String message) { super(message); }
}
