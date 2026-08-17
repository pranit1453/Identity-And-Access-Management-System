package com.pranit.accesshub.authentication.service.impl;

import com.pranit.accesshub.authentication.oauth2.service.OAuth2AuthenticationService;
import com.pranit.accesshub.authentication.repository.RefreshTokenRepository;
import com.pranit.accesshub.authorization.repository.UserRoleRepository;
import com.pranit.accesshub.entities.entity.RefreshToken;
import com.pranit.accesshub.entities.entity.User;
import com.pranit.accesshub.helper.Generate;
import com.pranit.accesshub.properties.TokenProperties;
import com.pranit.accesshub.security.model.UserDetail;
import com.pranit.accesshub.security.service.CookieService;
import com.pranit.accesshub.security.service.GenerateToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@NullMarked
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthenticationService oAuth2AuthenticationService;
    private final GenerateToken generateToken;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProperties tokenProperties;
    private final CookieService cookieService;
    private final UserRoleRepository userRoleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("In Oauth2 Authentication success Handlers");
        final OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        final OAuth2User user = (OAuth2User) authentication.getPrincipal();
        final User userData = oAuth2AuthenticationService.authenticate(token.getAuthorizedClientRegistrationId(), user);
        final UserDetail userDetail = convertUserDetail(userData);
        final UUID userId = userData.getUserId();
        if (userId != null) {
            refreshTokenRepository.revokeAllByUserId(userId);
        }
        final String jti = Generate.generateJti();
        final Instant now = Instant.now();
        final String sessionId = Generate.generateSessionId();
        final RefreshToken rt = RefreshToken.builder()
                .jti(jti)
                .sessionId(sessionId)
                .user(userData)
                .expiresAt(now.plus(Duration.ofSeconds(tokenProperties.refreshToken().expiration())))
                .revoked(false)
                .build();
        refreshTokenRepository.save(rt);
        final String accessToken = generateToken.generateAccessToken(userDetail, sessionId);
        final String refreshToken = generateToken.generateRefreshToken(userDetail, jti, sessionId);
        final Duration accessTokenTtl = Duration.ofSeconds(tokenProperties.accessToken().expiration());
        final Duration refreshTokenTtl = Duration.ofSeconds(tokenProperties.refreshToken().expiration());
        cookieService.attachAccessTokenCookie(response, accessToken, accessTokenTtl);
        cookieService.attachRefreshTokenCookie(response, refreshToken, refreshTokenTtl);
        cookieService.addNoStoreHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {"message":"Login successful"}
                """);

    }

    private UserDetail convertUserDetail(final User user) {
        final Set<GrantedAuthority> authorities = fetchAuthorities(user.getUserId());
        return UserDetail.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .enabled(user.isEnabled())
                .deleted(user.isDeleted())
                .mfaEnabled(user.isMfaEnabled())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .build();
    }

    private Set<GrantedAuthority> fetchAuthorities(UUID userId) {
        return userRoleRepository
                .findAuthoritiesByUserId(userId)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
