package com.pranit.accesshub.authentication.service.impl;

import com.pranit.accesshub.authentication.exception.UserNotExistsException;
import com.pranit.accesshub.authentication.repository.UserRepository;
import com.pranit.accesshub.authentication.service.UserDetailService;
import com.pranit.accesshub.authorization.repository.UserRoleRepository;
import com.pranit.accesshub.entities.entity.User;
import com.pranit.accesshub.security.model.UserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService, UserDetailService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public @NullMarked UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Username {} not found", username);
                    return new UsernameNotFoundException("Invalid username or password");
                });
        unlockIfLockExpired(user);
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

    private Set<GrantedAuthority> fetchAuthorities(final UUID userId) {
        return userRoleRepository.findAuthoritiesByUserId(userId)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @Transactional
    public UserDetail loadUserByUserId(final UUID userId) {
        final User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("UserId {} not found", userId);
                    return new UserNotExistsException("Invalid username or passwords");
                });
        unlockIfLockExpired(user);
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

    private void unlockIfLockExpired(final User user) {
        if (user.isAccountNonLocked()) return;
        final Instant lockedUntil = user.getLockedUntil();
        if (lockedUntil == null) {
            return;
        }
        if (Instant.now().isBefore(lockedUntil)) {
            log.warn("User {} has been unlocked", user.getUserId());
            throw new LockedException("Account is locked");
        }
        user.setAccountNonLocked(true);
        user.setAttempts(0);
        user.setLockedUntil(null);

        log.info("Account automatically unlocked. userId: {}", user.getUserId());
    }
}
