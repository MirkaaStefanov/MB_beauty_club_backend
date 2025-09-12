package com.example.MB_beauty_club_backend.exceptions.user;

import com.example.MB_beauty_club_backend.exceptions.common.BadRequestException;

/**
 * Exception thrown when there is an issue with user login, such as invalid email or password.
 */
public class UserLoginException extends BadRequestException {
    public UserLoginException() {
        super("Невалиден имейл или парола!");
    }
}
