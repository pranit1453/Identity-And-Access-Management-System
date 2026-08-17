package com.pranit.accesshub.controller;

import com.pranit.accesshub.authorization.constant.SortPermission;
import com.pranit.accesshub.authorization.dto.CreatePermissionRequest;
import com.pranit.accesshub.authorization.dto.PermissionResponse;
import com.pranit.accesshub.authorization.dto.UpdatePermissionRequest;
import com.pranit.accesshub.authorization.service.PermissionCrudService;
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

    private final PermissionCrudService permissionCrudService;

    @PostMapping
    public ResponseEntity<String> createPermission(@RequestBody @Valid CreatePermissionRequest request) {
        permissionCrudService.createNewPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Permission created Successfully");
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> createPermissions(@RequestBody @Valid Set<CreatePermissionRequest> request) {
        int roleCount = permissionCrudService.createNewPermissions(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleCount + " permissions created successfully");
    }

    @GetMapping("/{permissionId}")
    public ResponseEntity<PermissionResponse> fetchPermissionById(@PathVariable @NotNull Long permissionId) {
        final PermissionResponse response = permissionCrudService.fetchPermissionById(permissionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/permissions")
    public ResponseEntity<PageResponse<PermissionResponse>> fetchRoles(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "PERMISSION_NAME") SortPermission sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        final PageResponse<PermissionResponse> responses = permissionCrudService.fetchPermissions(page, size, keyword, sortBy, sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PutMapping("/update/{permissionId}")
    public ResponseEntity<PermissionResponse> updateRoleById(@RequestBody @Valid UpdatePermissionRequest request, @PathVariable @NotNull Long permissionId) {
        final PermissionResponse response = permissionCrudService.updatePermissionById(request, permissionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{permissionId}")
    public ResponseEntity<String> deleteRoleById(@PathVariable @NotNull Long permissionId) {
        permissionCrudService.deletePermissionById(permissionId);
        return ResponseEntity.status(HttpStatus.OK).body("Permission has been deleted");
    }
}
