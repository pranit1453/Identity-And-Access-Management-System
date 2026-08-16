package com.pranit.accesshub.authentication.seeder;

import com.pranit.accesshub.authentication.repository.UserRepository;
import com.pranit.accesshub.authorization.exception.RoleNotFoundException;
import com.pranit.accesshub.authorization.repository.RoleRepository;
import com.pranit.accesshub.authorization.repository.UserRoleRepository;
import com.pranit.accesshub.authorization.service.UserRoleAssignService;
import com.pranit.accesshub.entities.constant.Gender;
import com.pranit.accesshub.entities.constant.Provider;
import com.pranit.accesshub.entities.entity.Role;
import com.pranit.accesshub.entities.entity.User;
import com.pranit.accesshub.entities.entity.UserProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private static final String SUPER_ADMIN_ROLE = "SUPERADMIN";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleAssignService userRoleAssignService;
    private final UserRoleRepository userRoleRepository;

    @Value("${access-hub.credential.username:superadmin}")
    private String superAdminUsername;

    @Value("${access-hub.credential.password:access-hub@IAM14}")
    private String superAdminPassword;


    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments args) throws Exception {
        log.info("Starting application data seeding...");
        verifyAndSeedSuperAdmin();
        log.info("Application data seeding completed.");
    }

    private void verifyAndSeedSuperAdmin() {
        final Role role = findRole();
        if (userRoleRepository.existsByRole_RoleId(role.getRoleId())) {
            log.warn("User already exists with 'SUPERADMIN' role.'");
            return;
        }
        final User user = buildUser();
        final UserProfile userProfile = buildUserProfile();
        user.setUserProfile(userProfile);
        userProfile.setUser(user);
        final User saved = userRepository.save(user);
        userRoleAssignService.addRoleToUser(saved.getUserId(), role);
    }

    private Role findRole() {
        return roleRepository.findByRoleName(DataSeeder.SUPER_ADMIN_ROLE)
                .orElseThrow(() -> {
                    log.warn("Role not found with name: {}", DataSeeder.SUPER_ADMIN_ROLE);
                    return new RoleNotFoundException("SUPERADMIN role not found.");
                });
    }

    private User buildUser() {
        return User.builder()
                .username(superAdminUsername)
                .email("superadmin@gmail.com")
                .password(passwordEncoder.encode(superAdminPassword))
                .oauth2Id(null)
                .provider(Provider.LOCAL)
                .enabled(true)
                .deleted(false)
                .mfaEnabled(false)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
    }

    private UserProfile buildUserProfile() {
        return UserProfile.builder()
                .firstName("super")
                .lastName("admin")
                .dob(LocalDate.now())
                .gender(Gender.PREFER_NOT_TO_SAY)
                .address("India")
                .build();
    }
}
