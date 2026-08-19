package com.pranit.accesshub.controller;

import com.pranit.accesshub.authorization.constant.SortPermission;
import com.pranit.accesshub.authorization.dto.*;
import com.pranit.accesshub.authorization.service.RolePermissionAssignService;
import com.pranit.accesshub.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
@Validated
public class RolePermissionController {

    private final RolePermissionAssignService rolePermissionAssignService;

    @PostMapping(value = "/assign", version = "v1")
    public ResponseEntity<AssignResponse> assignPermissionsToRole(@RequestBody @Valid AssignRolePermissionRequest request) {
        final AssignResponse response = rolePermissionAssignService.assignPermissionsToRole(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/revoke", version = "v1")
    public ResponseEntity<RevokeResponse> revokePermissionFromRole(@RequestBody @Valid RevokeRolePermissionRequest request) {
        final RevokeResponse response = rolePermissionAssignService.revokePermissionFromRole(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{roleId}/permissions", version = "v1")
    public ResponseEntity<RoleWithPermissionsResponse> findPermissionByRoleId(@PathVariable @NotNull Long roleId) {
        final RoleWithPermissionsResponse responses = rolePermissionAssignService.findPermissionByRoleId(roleId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping(value = "/all", version = "v1")
    public ResponseEntity<PageResponse<RoleWithPermissionsResponse>> findAllPermissionForParticularRoles(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ROLE_PERMISSION_ID") SortPermission sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        final PageResponse<RoleWithPermissionsResponse> responses = rolePermissionAssignService.findAllPermissionForParticularRoles(page, size, keyword, sortBy, sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
