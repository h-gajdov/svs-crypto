package com.svsbrains.svscrypto.model.exceptions;

public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException(String username) {
        super("Username " + username + " is already in use");
    }
}
