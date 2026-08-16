package com.pranit.accesshub.redis.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pranit.accesshub.otp.constant.OtpPurpose;
import com.pranit.accesshub.otp.exception.OTPValidationException;
import com.pranit.accesshub.redis.model.OtpVault;
import com.pranit.accesshub.redis.service.RedisOtpStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisOtpStoreImpl implements RedisOtpStore {

    private static final String CHALLENGE_PREFIX = "otp:challenge:";
    private static final String CURRENT_PREFIX = "otp:current:";
    private static final String FAILURE_PREFIX = "otp:failures:";
    private static final String LOCK_PREFIX = "otp:lock:";

    private static final String LOCK_VALUE = "LOCKED";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;


    @Override
    public void saveChallenge(String challengeId, OtpVault vault, Duration ttl) {
        String key = challengeKey(vault.purpose(), challengeId);
        try {
            String value = objectMapper.writeValueAsString(vault);
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (JsonProcessingException e) {
            throw new OTPValidationException("Failed to serialize OTP challenge");
        }
    }

    @Override
    public Optional<OtpVault> getChallenge(String challengeId, OtpPurpose purpose) {
        String key = challengeKey(purpose, challengeId);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();


        try {
            OtpVault vault = objectMapper.readValue(value, OtpVault.class);
            /*
             * Extra safety check.
             *
             * The purpose supplied by the caller must match
             * the purpose stored inside the challenge.
             */
            if (vault.purpose() != purpose) return Optional.empty();

            return Optional.of(vault);
        } catch (JsonProcessingException e) {
            throw new OTPValidationException("Failed to deserialize OTP challenge");
        }
    }

    @Override
    public void deleteChallenge(String challengeId, OtpPurpose purpose) {
        redisTemplate.delete(challengeKey(purpose, challengeId));
    }

    @Override
    public void setCurrentChallenge(String email, OtpPurpose purpose, String challengeId, Duration ttl) {
        redisTemplate.opsForValue().set(currentKey(email, purpose), challengeId, ttl);
    }

    @Override
    public boolean isCurrentChallenge(String email, OtpPurpose purpose, String challengeId) {
        String currentChallenge = redisTemplate.opsForValue().get(currentKey(email, purpose));
        return challengeId.equals(currentChallenge);
    }

    @Override
    public int incrementFailures(String email, OtpPurpose purpose, Duration ttl) {
        String key = failureKey(email, purpose);
        Long failures = redisTemplate.opsForValue().increment(key);
        if (failures == null) throw new OTPValidationException("Failed to increment OTP failures");

        /*
         * First failure creates the key.
         * Give the counter a TTL.
         */
        if (failures == 1) redisTemplate.expire(key, ttl);

        return failures.intValue();
    }

    @Override
    public void clearFailures(String email, OtpPurpose purpose) {
        redisTemplate.delete(failureKey(email, purpose));
    }

    @Override
    public void lockOtpRequests(String email, Duration duration) {
        redisTemplate.opsForValue().set(lockKey(email), LOCK_VALUE, duration);
    }

    @Override
    public boolean isOtpRequestLocked(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey(email)));
    }

    @Override
    public void clearOtpState(String email, String challengeId, OtpPurpose purpose) {
        redisTemplate.delete(Set.of(
                challengeKey(purpose, challengeId),
                currentKey(email, purpose),
                failureKey(email, purpose)));
    }

    private String lockKey(String email) {
        return LOCK_PREFIX + normalizeEmail(email);
    }

    private String failureKey(String email, OtpPurpose purpose) {
        return FAILURE_PREFIX + purpose.name() + ":" + normalizeEmail(email);
    }

    private String currentKey(String email, OtpPurpose purpose) {
        return CURRENT_PREFIX + purpose.name() + ":" + normalizeEmail(email);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String challengeKey(OtpPurpose purpose, String challengeId) {
        return CHALLENGE_PREFIX + purpose.name() + ":" + challengeId;
    }
}