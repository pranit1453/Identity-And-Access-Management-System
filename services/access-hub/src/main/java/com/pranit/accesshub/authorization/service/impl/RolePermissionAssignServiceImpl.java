package com.pranit.accesshub.authorization.service.impl;

import com.pranit.accesshub.authorization.constant.SortPermission;
import com.pranit.accesshub.authorization.dto.*;
import com.pranit.accesshub.authorization.exception.PermissionNotAssignedToRole;
import com.pranit.accesshub.authorization.exception.PermissionNotFound;
import com.pranit.accesshub.authorization.exception.RoleNotFoundException;
import com.pranit.accesshub.authorization.repository.PermissionRepository;
import com.pranit.accesshub.authorization.repository.RolePermissionRepository;
import com.pranit.accesshub.authorization.repository.RoleRepository;
import com.pranit.accesshub.authorization.service.RolePermissionAssignService;
import com.pranit.accesshub.entities.entity.Permission;
import com.pranit.accesshub.entities.entity.Role;
import com.pranit.accesshub.entities.entity.RolePermission;
import com.pranit.accesshub.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class RolePermissionAssignServiceImpl implements RolePermissionAssignService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleCrudRepository;
    private final PermissionRepository permissionCrudRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AssignResponse assignPermissionsToRole(final AssignRolePermissionRequest request) {
        // Fetching role
        final Role role = validateAndFindRoleById(request.roleId());
        // fetching all permission IDs
        final Set<Long> permissionIds = Set.copyOf(request.permissionIds());
        final List<Permission> permissions = permissionCrudRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            log.warn("Some permissions do not exist. Requested IDs: {}", permissionIds);
            throw new PermissionNotFound("One or more permissions not found");
        }
        // Find already assigned permissions to avoid duplicates
        final Set<Long> existingPermissionIds = rolePermissionRepository
                .findByRoleRoleIdAndPermissionPermissionIdIn(role.getRoleId(), permissionIds)
                .stream()
                .map(rolePermission -> rolePermission.getPermission().getPermissionId())
                .collect(Collectors.toSet());
        // Create only new role-permission mappings
        final List<RolePermission> newRolePermissions = permissions.stream()
                .filter(permission -> !existingPermissionIds.contains(permission.getPermissionId()))
                .map(permission -> RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .build())
                .collect(Collectors.toList());
        if (!newRolePermissions.isEmpty()) {
            rolePermissionRepository.saveAll(newRolePermissions);
        }
        log.info("Assigned {} new permissions to role {}", newRolePermissions.size(), role.getRoleId());
        return AssignResponse.builder()
                .status(true)
                .message("Permissions are successfully assign to Role")
                .build();
    }

    private Role validateAndFindRoleById(final Long roleId) {
        return roleCrudRepository.findByRoleId(roleId)
                .orElseThrow(() -> {
                    log.warn("Role with ID {} not found", roleId);
                    return new RoleNotFoundException("Role not found");
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public RevokeResponse revokePermissionFromRole(final RevokeRolePermissionRequest request) {
        // Fetching role
        final Role role = validateAndFindRoleById(request.roleId());
        // fetching all permission IDs
        final Set<Long> permissionIds = Set.copyOf(request.permissionIds());
        final List<RolePermission> rolePermissions = rolePermissionRepository
                .findByRoleRoleIdAndPermissionPermissionIdIn(role.getRoleId(), permissionIds);
        if (rolePermissions.isEmpty()) {
            log.warn("No assigned permissions found for role {} with permissions {}", role.getRoleId(), permissionIds);
            throw new PermissionNotAssignedToRole("No requested permissions are assigned to this role");
        }
        int deletedCount = rolePermissionRepository
                .deleteByRoleIdAndPermissionIds(role.getRoleId(), permissionIds);
        if (deletedCount == 0) {
            throw new PermissionNotAssignedToRole("No requested permissions are assigned to this role");
        }
        log.info("Revoked {} permissions from role {}", rolePermissions.size(), role.getRoleId());
        return RevokeResponse.builder()
                .status(true)
                .message("Permissions successfully revoked from role")
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public RoleWithPermissionsResponse findPermissionByRoleId(final Long roleId) {
        final Role role = validateAndFindRoleById(roleId);
        List<RolePermissionResponse> permissions = rolePermissionRepository
                .fetchAllPermissionsAssociatedWithRoleId(role.getRoleId())
                .stream()
                .map(rp -> RolePermissionResponse.builder()
                        .permissionId(rp.getPermission().getPermissionId())
                        .permissionName(rp.getPermission().getPermissionName())
                        .permissionDescription(rp.getPermission().getPermissionDescription())
                        .build())
                .toList();
        return RoleWithPermissionsResponse.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .permissions(permissions)
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<RoleWithPermissionsResponse> findAllPermissionForParticularRoles(final int page, final int size, final String keyword, final SortPermission sortBy, final String sortDirection) {
        final Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(Sort.Direction.ASC, sortBy.getField())
                : Sort.by(Sort.Direction.DESC, sortBy.getField());
        final Pageable pageable = PageRequest.of(page, size, sort);
        final Page<Role> rolePage = roleCrudRepository.findAllByKeyword(keyword, pageable);
        final List<Long> roleIds = rolePage.getContent()
                .stream()
                .map(Role::getRoleId)
                .toList();
        final Map<Long, List<RolePermissionResponse>> permissionsMap;
        if (roleIds.isEmpty()) permissionsMap = Collections.emptyMap();
        else {
            final List<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleIds(roleIds);
            permissionsMap = rolePermissions.stream()
                    .collect(Collectors.groupingBy(
                            rp -> rp.getRole().getRoleId(),
                            Collectors.mapping(
                                    rp -> RolePermissionResponse.builder()
                                            .permissionId(rp.getPermission().getPermissionId())
                                            .permissionName(rp.getPermission().getPermissionName())
                                            .permissionDescription(rp.getPermission().getPermissionDescription())
                                            .build(),
                                    Collectors.toList())));
        }
        final List<RoleWithPermissionsResponse> content = rolePage.getContent()
                .stream()
                .map(role -> RoleWithPermissionsResponse.builder()
                        .roleId(role.getRoleId())
                        .roleName(role.getRoleName())
                        .permissions(permissionsMap.getOrDefault(role.getRoleId(), Collections.emptyList()))
                        .build()
                )
                .toList();
        return PageResponse.<RoleWithPermissionsResponse>builder()
                .content(content)
                .page(rolePage.getNumber())
                .size(rolePage.getSize())
                .totalElements(rolePage.getTotalElements())
                .totalPages(rolePage.getTotalPages())
                .last(rolePage.isLast())
                .build();
    }
}
