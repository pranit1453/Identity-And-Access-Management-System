package com.pranit.accesshub.authorization.repository;

import com.pranit.accesshub.entities.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query(value = """
                    (
                SELECT CONCAT('ROLE_', r.role_name) AS authority
                FROM auth.user_roles ur
                JOIN auth.roles r
                    ON ur.role_id = r.role_id
                JOIN auth.users u
                    ON ur.user_id = u.user_id
                WHERE u.user_id = :userId
                  AND u.enabled = TRUE
                  AND u.deleted = FALSE
                  AND u.account_non_locked = TRUE
                  AND u.account_non_expired = TRUE
                  AND u.credentials_non_expired = TRUE
                  AND ur.status = 'ACTIVE'
            )
            
            UNION
            
            (
                SELECT p.permission_name AS authority
                FROM auth.user_roles ur
                JOIN auth.role_permissions rp
                    ON ur.role_id = rp.role_id
                JOIN auth.permissions p
                    ON rp.permission_id = p.permission_id
                JOIN auth.users u
                    ON ur.user_id = u.user_id
                WHERE u.user_id = :userId
                  AND u.enabled = TRUE
                  AND u.deleted = FALSE
                  AND u.account_non_locked = TRUE
                  AND u.account_non_expired = TRUE
                  AND u.credentials_non_expired = TRUE
                  AND ur.status = 'ACTIVE'
            )
            """, nativeQuery = true)
    List<String> findAuthoritiesByUserId(@Param("userId") UUID userId);

    boolean existsByUser_UserIdAndRole_RoleId(UUID userId, Long roleId);

    boolean existsByRole_RoleId(Long roleId);
}
