package com.pranit.accesshub.authentication.repository;

import com.pranit.accesshub.entities.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    @Query("""
                SELECT up
                FROM UserProfile up
                JOIN FETCH up.user uc
                WHERE up.userId = :userId
            """)
    Optional<UserProfile> findByUserId(UUID userId);
}
