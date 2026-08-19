package com.pranit.accesshub.authorization.service;

import com.pranit.accesshub.authorization.constant.SortRole;
import com.pranit.accesshub.authorization.dto.CreateRoleRequest;
import com.pranit.accesshub.authorization.dto.RoleResponse;
import com.pranit.accesshub.authorization.dto.UpdateRoleRequest;
import com.pranit.accesshub.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public interface RoleService {

    void createNewRole(@Valid CreateRoleRequest request);

    int createNewRoles(@Valid Set<CreateRoleRequest> request);

    RoleResponse fetchRoleById(@NotNull Long roleId);

    PageResponse<RoleResponse> fetchRoles(int page, int size, String keyword, SortRole sortBy, String sortDirection);

    RoleResponse updateRoleById(@Valid UpdateRoleRequest request, @NotNull Long roleId);

    void deleteRoleById(@NotNull Long roleId);
}
