package com.pranit.accesshub.authentication.service.impl;

import com.pranit.accesshub.authentication.dto.LoginRequest;
import com.pranit.accesshub.authentication.dto.LoginRespone;
import com.pranit.accesshub.authentication.repository.RefreshTokenRepository;
import com.pranit.accesshub.authentication.repository.UserRepository;
import com.pranit.accesshub.authentication.service.LoginAttempts;
import com.pranit.accesshub.authentication.service.LoginService;
import com.pranit.accesshub.authentication.service.RestoreUserAccount;
import com.pranit.accesshub.entities.entity.RefreshToken;
import com.pranit.accesshub.entities.entity.User;
import com.pranit.accesshub.helper.Generate;
import com.pranit.accesshub.properties.TokenProperties;
import com.pranit.accesshub.security.exception.UnauthorizedException;
import com.pranit.accesshub.security.model.UserDetail;
import com.pranit.accesshub.security.service.CookieService;
import com.pranit.accesshub.security.service.GenerateToken;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final TokenProperties tokenProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GenerateToken generateToken;
    private final CookieService cookieService;
    private final UserRepository userRepository;
    private final LoginAttempts loginAttempts;
    private final RestoreUserAccount restoreUserAccount;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public LoginRespone authenticateUser(LoginRequest request, HttpServletResponse response) {
        log.debug("Authentication attempt initiated for username: {}", request.username());
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate
                    (UsernamePasswordAuthenticationToken.unauthenticated
                            (request.username(), request.password()));
        } catch (BadCredentialsException exception) {
            loginAttempts.handleFailedLogin(request.username());
            throw new BadCredentialsException("Invalid username or password");
        }
        loginAttempts.handleSuccessfulLogin(request.username());
        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetail userDetail)) {
            log.warn("Invalid authentication principal returned for username: {}", request.username());
            throw new UnauthorizedException("Invalid authentication principal");
        }

        log.debug("Authentication successful username: {} userId: {}", userDetail.username(), userDetail.userId());

        UserDetail authenticateUserDetail = userDetail;
        if (userDetail.deleted()) {
            restoreUserAccount.restoreUserAccount(authenticateUserDetail.userId());
            authenticateUserDetail = UserDetail.builder()
                    .userId(authenticateUserDetail.userId())
                    .username(authenticateUserDetail.username())
                    .password(authenticateUserDetail.password())
                    .authorities(authenticateUserDetail.authorities())
                    .deleted(false)
                    .enabled(authenticateUserDetail.enabled())
                    .mfaEnabled(authenticateUserDetail.mfaEnabled())
                    .accountNonExpired(authenticateUserDetail.accountNonExpired())
                    .accountNonLocked(authenticateUserDetail.accountNonLocked())
                    .credentialsNonExpired(authenticateUserDetail.credentialsNonExpired())
                    .build();
        }

        final Map<Boolean, Set<String>> grouped = authenticateUserDetail.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .collect(Collectors.partitioningBy(
                        a -> a.startsWith("ROLE_"),
                        Collectors.toUnmodifiableSet()));
        Set<String> roles = grouped.get(true);
        Set<String> permissions = grouped.get(false);

        // MFA check
        if (authenticateUserDetail.mfaEnabled()) {
            log.info("MFA verification required for userId: {}", userDetail.userId());
            // Generate OTP
            // Send OTP
            throw new UnauthorizedException("MFA verification is not supported.");
        }
        final String jti = Generate.generateJti();
        final Instant now = Instant.now();
        final User user = userRepository.getReferenceById(authenticateUserDetail.userId());
        final String sessionId = Generate.generateSessionId();
        final RefreshToken rt = RefreshToken.builder()
                .jti(jti)
                .sessionId(sessionId)
                .user(user)
                .expiresAt(now.plus(Duration.ofSeconds(tokenProperties.refreshToken().expiration())))
                .revoked(false)
                .build();
        refreshTokenRepository.save(rt);
        final String accessToken = generateToken.generateAccessToken(authenticateUserDetail, sessionId);
        final String refreshToken = generateToken.generateRefreshToken(authenticateUserDetail, jti, sessionId);
        final Duration accessTokenTtl = Duration.ofSeconds(tokenProperties.accessToken().expiration());
        final Duration refreshTokenTtl = Duration.ofSeconds(tokenProperties.refreshToken().expiration());
        cookieService.attachAccessTokenCookie(response, accessToken, accessTokenTtl);
        cookieService.attachRefreshTokenCookie(response, refreshToken, refreshTokenTtl);
        cookieService.addNoStoreHeaders(response);
        log.info("User logged in successfully userId: {}", authenticateUserDetail.userId());
        return LoginRespone.builder()
                .username(authenticateUserDetail.username())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}
