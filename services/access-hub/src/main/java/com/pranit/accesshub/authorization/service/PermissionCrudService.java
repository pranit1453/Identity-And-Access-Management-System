package com.pranit.accesshub.authorization.service;

import com.pranit.accesshub.authorization.constant.SortPermission;
import com.pranit.accesshub.authorization.dto.CreatePermissionRequest;
import com.pranit.accesshub.authorization.dto.PermissionResponse;
import com.pranit.accesshub.authorization.dto.UpdatePermissionRequest;
import com.pranit.accesshub.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public interface PermissionCrudService {

    void createNewPermission(@Valid CreatePermissionRequest request);

    int createNewPermissions(@Valid Set<CreatePermissionRequest> request);

    PermissionResponse fetchPermissionById(@NotNull Long permissionId);

    PageResponse<PermissionResponse> fetchPermissions(int page, int size, String keyword, SortPermission sortBy, String sortDirection);

    PermissionResponse updatePermissionById(@Valid UpdatePermissionRequest request, @NotNull Long permissionId);

    void deletePermissionById(@NotNull Long permissionId);
}
