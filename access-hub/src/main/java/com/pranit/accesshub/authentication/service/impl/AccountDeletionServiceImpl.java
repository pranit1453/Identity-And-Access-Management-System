package com.pranit.accesshub.authentication.service.impl;

import com.pranit.accesshub.authentication.exception.UserNotExistsException;
import com.pranit.accesshub.authentication.repository.RefreshTokenRepository;
import com.pranit.accesshub.authentication.repository.UserRepository;
import com.pranit.accesshub.authentication.service.AccountDeletionService;
import com.pranit.accesshub.entities.entity.User;
import com.pranit.accesshub.event.dto.DeactivateAccountEvent;
import com.pranit.accesshub.event.service.EventPublisherService;
import com.pranit.accesshub.helper.Generate;
import com.pranit.accesshub.helper.SecurityContext;
import com.pranit.accesshub.redis.service.RedisTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDeletionServiceImpl implements AccountDeletionService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTokenStore redisTokenStore;
    private final EventPublisherService eventPublisherService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserAccount() {
        final UUID userId = SecurityContext.getCurrentUserId();
        final User user = findUserByUserId(userId);
        if (user.isDeleted()) {
            log.info("User account already scheduled for deletion: {}", userId);
            return;
        }
        final Instant deletionDate = Instant.now().plus(15, ChronoUnit.DAYS);
        user.setDeleted(true);
        user.setScheduledDeletionAt(deletionDate);
        refreshTokenRepository.revokeAllByUserId(userId);
        redisTokenStore.invalidateUserSession(userId);
        log.info("User account deleted for userId: {}", userId);
        final DeactivateAccountEvent event = DeactivateAccountEvent.builder()
                .eventId(Generate.generateEventId())
                .username(user.getUsername())
                .email(user.getEmail())
                .timestamp(Instant.now())
                .build();
        eventPublisherService.sendSecurityAlertToUser(event);
    }

    private User findUserByUserId(final UUID userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with userId: {}", userId);
                    return new UserNotExistsException("User not found");
                });
    }
}
