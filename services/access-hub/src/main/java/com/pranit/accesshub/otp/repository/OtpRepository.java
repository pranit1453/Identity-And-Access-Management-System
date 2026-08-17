package com.pranit.accesshub.otp.repository;

import com.pranit.accesshub.entities.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    Optional<OtpEntity> findByChallengeId(String challengeId);
}
