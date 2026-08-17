package com.pranit.accesshub.authorization.repository;

import com.pranit.accesshub.entities.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    boolean existsByPermissionName(String permissionName);

    Set<Permission> findAllByPermissionNameIn(Set<String> permissionNames);

    Optional<Permission> findByPermissionId(Long permissionId);
}
