package com.pranit.accesshub.authorization.repository;

import com.pranit.accesshub.entities.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long>, JpaSpecificationExecutor<RolePermission> {

    List<RolePermission> findByRoleRoleIdAndPermissionPermissionIdIn(Long roleId, Set<Long> permissionIds);

    @Modifying
    @Query("""
                DELETE FROM RolePermission rp
                WHERE rp.role.roleId = :roleId
                AND rp.permission.permissionId IN :permissionIds
            """)
    int deleteByRoleIdAndPermissionIds(@Param("roleId") Long roleId, @Param("permissionIds") Set<Long> permissionIds);

    @Query("""
                SELECT rp
                FROM RolePermission rp
                JOIN FETCH rp.permission p
                JOIN FETCH rp.role r
                WHERE r.roleId = :roleId
            """)
    List<RolePermission> fetchAllPermissionsAssociatedWithRoleId(@Param("roleId") Long roleId);

    @Query("""
                SELECT rp
                FROM RolePermission rp
                JOIN FETCH rp.permission p
                JOIN FETCH rp.role r
                WHERE r.id IN :roleIds
            """)
    List<RolePermission> findAllByRoleIds(@Param("roleIds") List<Long> roleIds);
    
}
