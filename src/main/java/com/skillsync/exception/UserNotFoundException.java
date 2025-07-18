package com.skillsync.exception;

import java.io.Serial;

public class UserNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}
