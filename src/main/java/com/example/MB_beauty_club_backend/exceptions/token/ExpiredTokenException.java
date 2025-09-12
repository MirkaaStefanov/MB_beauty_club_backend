package com.example.MB_beauty_club_backend.exceptions.token;

import com.example.MB_beauty_club_backend.exceptions.common.UnauthorizedException;

public class ExpiredTokenException extends UnauthorizedException {
    public ExpiredTokenException() {
        super("Токенът е изтекъл!");
    }
}
