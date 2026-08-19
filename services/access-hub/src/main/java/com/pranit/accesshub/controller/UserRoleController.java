package com.pranit.accesshub.controller;

import com.pranit.accesshub.authorization.dto.*;
import com.pranit.accesshub.authorization.service.UserRoleAssignService;
import com.pranit.accesshub.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assign/role")
@RequiredArgsConstructor
@Validated
public class UserRoleController {

    private final UserRoleAssignService userRoleAssignService;

    @PostMapping(value = "/assign", version = "v1")
    public ResponseEntity<AssignResponse> assignRoleToUser(@RequestBody @Valid AssignUserRoleRequest request) {
        final AssignResponse response = userRoleAssignService.assignRoleToUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping(value = "/revoke/{userRoleId}", version = "v1")
    public ResponseEntity<RevokeResponse> revokeRoleAssignToUser(@PathVariable @NotNull Long userRoleId, @RequestBody @Valid RevokeUserRoleRequest request) {
        final RevokeResponse response = userRoleAssignService.revokeRoleAssignedToUser(userRoleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping(value = "/assign/{userRoleId}", version = "v1")
    public ResponseEntity<AssignResponse> reAssignRoleToUser(@PathVariable @NotNull Long userRoleId, @RequestBody @Valid AssignUserRoleRequest request) {
        final AssignResponse response = userRoleAssignService.reAssignRoleToUser(userRoleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/get/{userRoleId}", version = "v1")
    public ResponseEntity<UserRoleResponse> fetchUserRoleById(@PathVariable @NotNull Long userRoleId) {
        final UserRoleResponse response = userRoleAssignService.fetchUserRoleById(userRoleId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/all", version = "v1")
    public ResponseEntity<PageResponse<UserRoleResponse>> findAllRolesForParticularUser(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "userRoleId") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        final PageResponse<UserRoleResponse> responses = userRoleAssignService.findAllRolesForParticularUser(page, size, keyword, sortBy, sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
