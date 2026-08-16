package com.pranit.accesshub.authentication.service.impl;

import com.pranit.accesshub.authentication.repository.UserRepository;
import com.pranit.accesshub.authentication.service.LoginAttempts;
import com.pranit.accesshub.entities.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptsImpl implements LoginAttempts {

    private static final int MAX_ATTEMPTS = 3;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleFailedLogin(final String username) {
        final User user = validateAndFindUserByUsername(username);
        final int attempts = user.getAttempts() + 1;
        user.setAttempts(attempts);
        if (attempts >= MAX_ATTEMPTS) {
            user.setAccountNonLocked(false);
            user.setLockedUntil(Instant.now().plus(1, ChronoUnit.DAYS));
            log.warn("User account locked after {} failed login attempts. userId: {}", attempts, user.getUserId());
        } else {
            log.warn("Failed login attempt {} for userId: {}", attempts, user.getUserId());
        }
    }

    private User validateAndFindUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User {} not found", username);
                    return new UsernameNotFoundException("Invalid username or password");
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleSuccessfulLogin(final String username) {
        final User user = validateAndFindUserByUsername(username);
        user.setAttempts(0);
        log.info("Failed login attempts reset for userId: {}", user.getUserId());
    }

}
