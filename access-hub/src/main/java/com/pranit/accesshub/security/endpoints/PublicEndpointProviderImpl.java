package com.pranit.accesshub.security.endpoints;

import org.springframework.stereotype.Service;

@Service
public class PublicEndpointProviderImpl implements PublicEndpointProvider {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/register",
            "/api/register/admin",
            "/api/auth/login",
            "/api/password/reset/request",
            "/api/password/reset/verify",
            "/api/password/reset",
            "/api/verify",
            "/api/verify/resend",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    @Override
    public String[] publicEndpoints() {
        return PUBLIC_ENDPOINTS;
    }
}
