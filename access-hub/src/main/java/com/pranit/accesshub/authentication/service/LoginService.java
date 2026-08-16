package com.pranit.accesshub.authentication.service;

import com.pranit.accesshub.authentication.dto.LoginRequest;
import com.pranit.accesshub.authentication.dto.LoginRespone;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@FunctionalInterface
public interface LoginService {
    
    LoginRespone authenticateUser(@Valid LoginRequest request, @Valid HttpServletResponse response);
}
