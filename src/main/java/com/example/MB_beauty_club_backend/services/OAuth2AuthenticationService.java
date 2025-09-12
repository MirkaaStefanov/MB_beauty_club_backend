package com.example.MB_beauty_club_backend.services;

import com.example.MB_beauty_club_backend.models.dto.auth.AuthenticationResponse;

public interface OAuth2AuthenticationService {

    String getOAuthGoogleLoginUrl();

    AuthenticationResponse processOAuthGoogleLogin(String code);
}
