package com.usersaddresses.errorhandling;

public class UserNotExistsException extends RuntimeException {
    public UserNotExistsException(String msg) {
        super(msg);
    }
}
