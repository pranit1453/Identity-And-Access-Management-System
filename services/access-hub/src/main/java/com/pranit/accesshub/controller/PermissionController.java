package com.pranit.accesshub.controller;

import com.pranit.accesshub.authorization.constant.SortPermission;
import com.pranit.accesshub.authorization.dto.CreatePermissionRequest;
import com.pranit.accesshub.authorization.dto.PermissionResponse;
import com.pranit.accesshub.authorization.dto.UpdatePermissionRequest;
import com.pranit.accesshub.authorization.service.PermissionService;
import com.pranit.accesshub.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
@Validated
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping(version = "v1")
    public ResponseEntity<String> createPermission(@RequestBody @Valid CreatePermissionRequest request) {
        permissionService.createNewPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Permission created Successfully");
    }

    @PostMapping(value = "/bulk", version = "v1")
    public ResponseEntity<String> createPermissions(@RequestBody @Valid Set<CreatePermissionRequest> request) {
        int roleCount = permissionService.createNewPermissions(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleCount + " permissions created successfully");
    }

    @GetMapping(value = "/{permissionId}", version = "v1")
    public ResponseEntity<PermissionResponse> fetchPermissionById(@PathVariable @NotNull Long permissionId) {
        final PermissionResponse response = permissionService.fetchPermissionById(permissionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/permissions", version = "v1")
    public ResponseEntity<PageResponse<PermissionResponse>> fetchRoles(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "PERMISSION_NAME") SortPermission sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        final PageResponse<PermissionResponse> responses = permissionService.fetchPermissions(page, size, keyword, sortBy, sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PutMapping(value = "/update/{permissionId}", version = "v1")
    public ResponseEntity<PermissionResponse> updateRoleById(@RequestBody @Valid UpdatePermissionRequest request, @PathVariable @NotNull Long permissionId) {
        final PermissionResponse response = permissionService.updatePermissionById(request, permissionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/delete/{permissionId}", version = "v1")
    public ResponseEntity<String> deleteRoleById(@PathVariable @NotNull Long permissionId) {
        permissionService.deletePermissionById(permissionId);
        return ResponseEntity.status(HttpStatus.OK).body("Permission has been deleted");
    }
}
