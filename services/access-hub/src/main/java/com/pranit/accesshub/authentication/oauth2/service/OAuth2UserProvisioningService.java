package com.pranit.accesshub.authentication.oauth2.service;

import com.pranit.accesshub.authentication.exception.UserNotExistsException;
import com.pranit.accesshub.authentication.oauth2.model.OAuth2UserInfo;
import com.pranit.accesshub.authentication.repository.UserRepository;
import com.pranit.accesshub.authorization.exception.RoleAlreadyAssignedException;
import com.pranit.accesshub.authorization.exception.RoleNotFoundException;
import com.pranit.accesshub.authorization.repository.RoleRepository;
import com.pranit.accesshub.authorization.repository.UserRoleRepository;
import com.pranit.accesshub.entities.constant.Gender;
import com.pranit.accesshub.entities.constant.Status;
import com.pranit.accesshub.entities.entity.Role;
import com.pranit.accesshub.entities.entity.User;
import com.pranit.accesshub.entities.entity.UserProfile;
import com.pranit.accesshub.entities.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserProvisioningService {

    private static final String DEFAULT_ROLE = "USER";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public User provisionUserCredential(final OAuth2UserInfo info) {
        log.debug("Checking existing OAuth2 user with provider: {}", info.provider());
        return userRepository.findByEmail(info.email())
                .map(existingUser -> {
                    log.info("Existing OAuth2 user authenticated. userId: {}, provider: {}",
                            existingUser.getUserId(), info.provider());
                    return existingUser;
                })
                .orElseGet(() -> createUser(info));
    }

    private User createUser(final OAuth2UserInfo info) {
        log.info("Provisioning new OAuth2 user. provider: {}", info.provider());
        final Role defaultRole = roleRepository.findByRoleName(DEFAULT_ROLE)
                .orElseThrow(() -> {
                    log.warn("Default role not found during OAuth2 provisioning. role: {}",
                            DEFAULT_ROLE);
                    return new RoleNotFoundException("Role not found");
                });
        final User detail = User.builder()
                .email(info.email())
                .username(info.username())
                .oauth2Id(info.oauth2Id())
                .provider(info.provider())
                .enabled(true)
                .build();
        final UserProfile profile = UserProfile.builder()
                .firstName(info.firstName())
                .lastName(info.lastName())
                .dob(LocalDate.of(2000, 1, 1))
                .gender(Gender.PREFER_NOT_TO_SAY)
                .address("")
                .build();
        detail.setUserProfile(profile);
        User savedUser = userRepository.save(detail);
        addRoleToUser(savedUser.getUserId(), defaultRole);
        log.info("OAuth2 user provisioned successfully. userId: {}, provider: {}, assignedRole: {}",
                savedUser.getUserId(), info.provider(), DEFAULT_ROLE);
        return savedUser;
    }

    private void addRoleToUser(final UUID userId, final Role role) {
        final User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("No user credentials found for userId {}", userId);
                    return new UserNotExistsException("User not found");
                });
        if (userRoleRepository.existsByUser_UserIdAndRole_RoleId(userId, role.getRoleId())) {
            log.debug("Role '{}' already assigned to userId: {}", role.getRoleName(), userId);
            throw new RoleAlreadyAssignedException("Role already assigned");
        }
        final UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .status(Status.ACTIVE)
                .build();
        userRoleRepository.save(userRole);
        log.info("Assigned role '{}' to userId: {}", role.getRoleName(), userId);
    }
}
