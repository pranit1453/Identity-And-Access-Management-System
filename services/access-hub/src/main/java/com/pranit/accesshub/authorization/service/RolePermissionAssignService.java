package com.pranit.accesshub.authorization.service;

import com.pranit.accesshub.authorization.constant.SortPermission;
import com.pranit.accesshub.authorization.dto.*;
import com.pranit.accesshub.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface RolePermissionAssignService {

    AssignResponse assignPermissionsToRole(@Valid AssignRolePermissionRequest request);

    RevokeResponse revokePermissionFromRole(@Valid RevokeRolePermissionRequest request);

    RoleWithPermissionsResponse findPermissionByRoleId(@NotNull Long roleId);

    PageResponse<RoleWithPermissionsResponse> findAllPermissionForParticularRoles(int page, int size, String keyword, SortPermission sortBy, String sortDirection);
}
