package com.pranit.accesshub.authorization.service;

import com.pranit.accesshub.authorization.dto.*;
import com.pranit.accesshub.entities.entity.Role;
import com.pranit.accesshub.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface UserRoleAssignService {

    void addRoleToUser(UUID userId, Role role);

    AssignResponse assignRoleToUser(@Valid AssignUserRoleRequest request);

    RevokeResponse revokeRoleAssignedToUser(@NotNull Long userRoleId, @Valid RevokeUserRoleRequest request);

    AssignResponse reAssignRoleToUser(@NotNull Long userRoleId, @Valid AssignUserRoleRequest request);

    UserRoleResponse fetchUserRoleById(@NotNull Long userRoleId);

    PageResponse<UserRoleResponse> findAllRolesForParticularUser(int page, int size, String keyword, String sortBy, String sortDirection);
}
