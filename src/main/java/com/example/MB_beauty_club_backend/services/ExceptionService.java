package com.example.MB_beauty_club_backend.services;

import com.example.MB_beauty_club_backend.exceptions.common.ApiException;

public interface ExceptionService {

    void log(ApiException runtimeException);

    void log(RuntimeException runtimeException, int statusCode);
}
