package com.example.MB_beauty_club_backend.exceptions.user;

import com.example.MB_beauty_club_backend.exceptions.common.ValidationException;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

public class UserValidationException extends ValidationException {
    public UserValidationException(Set<ConstraintViolation<?>> validationErrors) {
        super(validationErrors);
    }
}
