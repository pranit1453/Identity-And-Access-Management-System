package com.pranit.accesshub.authorization.repository;

import com.pranit.accesshub.entities.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
