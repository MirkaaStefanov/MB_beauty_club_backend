package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.exceptions.common.ApiException;
import com.example.MB_beauty_club_backend.models.entity.Exception;
import com.example.MB_beauty_club_backend.repositories.ExceptionRepository;
import com.example.MB_beauty_club_backend.services.ExceptionService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@AllArgsConstructor
public class ExceptionServiceImpl implements ExceptionService {
    private final ExceptionRepository exceptionRepository;

    @Override
    @Async
    public void log(ApiException apiException) {
        Exception exception = Exception.mapFromApiException(apiException);
        exceptionRepository.save(exception);
    }

    @Override
    @Async
    public void log(RuntimeException runtimeException, int statusCode) {
        Exception exception = Exception.mapFromRuntimeException(runtimeException, statusCode);
        exceptionRepository.save(exception);
    }
}
