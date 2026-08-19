package com.pranit.accesshub.controller;

import com.pranit.accesshub.authorization.constant.SortRole;
import com.pranit.accesshub.authorization.dto.CreateRoleRequest;
import com.pranit.accesshub.authorization.dto.RoleResponse;
import com.pranit.accesshub.authorization.dto.UpdateRoleRequest;
import com.pranit.accesshub.authorization.service.RoleService;
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
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final RoleService roleService;

    @PostMapping(version = "v1")
    public ResponseEntity<String> createRole(@RequestBody @Valid CreateRoleRequest request) {
        roleService.createNewRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Role created Successfully");
    }

    @PostMapping(value = "/bulk", version = "v1")
    public ResponseEntity<String> createRoles(@RequestBody @Valid Set<CreateRoleRequest> request) {
        int roleCount = roleService.createNewRoles(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleCount + " roles created successfully");
    }

    @GetMapping(value = "/{roleId}", version = "v1")
    public ResponseEntity<RoleResponse> fetchRoleById(@PathVariable @NotNull Long roleId) {
        final RoleResponse response = roleService.fetchRoleById(roleId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/roles", version = "v1")
    public ResponseEntity<PageResponse<RoleResponse>> fetchRoles(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ROLE_NAME") SortRole sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        final PageResponse<RoleResponse> responses = roleService.fetchRoles(page, size, keyword, sortBy, sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PutMapping(value = "/update/{roleId}", version = "v1")
    public ResponseEntity<RoleResponse> updateRoleById(@RequestBody @Valid UpdateRoleRequest request, @PathVariable @NotNull Long roleId) {
        final RoleResponse response = roleService.updateRoleById(request, roleId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/delete/{roleId}", version = "v1")
    public ResponseEntity<String> deleteRoleById(@PathVariable @NotNull Long roleId) {
        roleService.deleteRoleById(roleId);
        return ResponseEntity.status(HttpStatus.OK).body("Role has been deleted");
    }

}
