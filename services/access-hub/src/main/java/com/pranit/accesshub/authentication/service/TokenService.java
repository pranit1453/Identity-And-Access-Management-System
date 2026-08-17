package com.pranit.accesshub.authentication.service;

import com.pranit.accesshub.authentication.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface TokenService {
    
    Optional<String> readRefreshTokenFromRequest(HttpServletRequest request);

    TokenResponse generateNewRefreshToken(String refreshToken, HttpServletResponse response);
}
