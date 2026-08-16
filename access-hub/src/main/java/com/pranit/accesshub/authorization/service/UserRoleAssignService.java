package com.pranit.accesshub.authorization.service;

import com.pranit.accesshub.entities.entity.Role;

import java.util.UUID;

public interface UserRoleAssignService {
    
    void addRoleToUser(UUID userId, Role role);
}
