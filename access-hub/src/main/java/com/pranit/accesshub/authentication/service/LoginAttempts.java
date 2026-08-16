package com.pranit.accesshub.authentication.service;

public interface LoginAttempts {

    void handleFailedLogin(String username);

    void handleSuccessfulLogin(String username);
}
