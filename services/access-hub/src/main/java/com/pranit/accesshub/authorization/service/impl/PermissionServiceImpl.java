package com.pranit.accesshub.authorization.service.impl;

import com.pranit.accesshub.authorization.constant.SortPermission;
import com.pranit.accesshub.authorization.dto.CreatePermissionRequest;
import com.pranit.accesshub.authorization.dto.PermissionResponse;
import com.pranit.accesshub.authorization.dto.UpdatePermissionRequest;
import com.pranit.accesshub.authorization.exception.DuplicatePermissionName;
import com.pranit.accesshub.authorization.exception.PermissionAlreadyExists;
import com.pranit.accesshub.authorization.exception.PermissionNotFound;
import com.pranit.accesshub.authorization.mapper.PermissionMapper;
import com.pranit.accesshub.authorization.repository.PermissionRepository;
import com.pranit.accesshub.authorization.service.PermissionService;
import com.pranit.accesshub.authorization.specification.PermissionSpecification;
import com.pranit.accesshub.entities.entity.Permission;
import com.pranit.accesshub.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionCrudRepository;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewPermission(final CreatePermissionRequest request) {
        if (permissionCrudRepository.existsByPermissionName(request.permissionName())) {
            log.warn("Permission with name {} already exists", request.permissionName());
            throw new PermissionAlreadyExists("Permission already exists");
        }
        final Permission permission = permissionMapper.toPermission(request);
        final Permission save = permissionCrudRepository.save(permission);
        log.debug("Permission {} created", save.getPermissionName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int createNewPermissions(final Set<CreatePermissionRequest> request) {
        final Set<String> permissionNames = request.stream()
                .map(CreatePermissionRequest::permissionName)
                .collect(Collectors.toUnmodifiableSet());
        final Set<String> existingPermissionNames = permissionCrudRepository.findAllByPermissionNameIn(permissionNames)
                .stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toUnmodifiableSet());
        final List<Permission> permissionsToSave = request.stream()
                .filter(req -> {
                    final boolean exists = existingPermissionNames.contains(req.permissionName());
                    if (exists) log.warn("Skipping role '{}' because it already exists.", req.permissionName());
                    return !exists;
                }).map(permissionMapper::toPermission)
                .toList();
        if (permissionsToSave.isEmpty()) {
            log.info("No new roles to create.");
            return 0;
        }
        permissionCrudRepository.saveAll(permissionsToSave);
        log.info("Created {} new roles.", permissionsToSave.size());
        return permissionsToSave.size();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PermissionResponse fetchPermissionById(final Long permissionId) {
        final Permission permission = validateAndFindPermissionById(permissionId);
        return permissionMapper.toResponse(permission);
    }

    private Permission validateAndFindPermissionById(final Long permissionId) {
        log.debug("Validating and fetching permission ID: {}", permissionId);
        return permissionCrudRepository.findByPermissionId(permissionId)
                .orElseThrow(() -> {
                    log.warn("Permission {} not found", permissionId);
                    return new PermissionNotFound("Permission not found");
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PageResponse<PermissionResponse> fetchPermissions(final int page, final int size, final String keyword, final SortPermission sortBy, final String sortDirection) {
        final Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(Sort.Direction.ASC, sortBy.getField())
                : Sort.by(Sort.Direction.DESC, sortBy.getField());
        final Pageable pageable = PageRequest.of(page, size, sort);
        final Specification<Permission> specification = PermissionSpecification.searchKeyword(keyword);
        final Page<Permission> pages = permissionCrudRepository.findAll(specification, pageable);
        final List<PermissionResponse> content = pages.getContent()
                .stream()
                .map(permissionMapper::toResponse)
                .toList();
        log.debug("Fetch {} permissions", pages.getTotalElements());
        return PageResponse.<PermissionResponse>builder()
                .content(content)
                .page(pages.getNumber())
                .size(pages.getSize())
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .last(pages.isLast())
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PermissionResponse updatePermissionById(final UpdatePermissionRequest request, final Long permissionId) {
        final Permission permission = validateAndFindPermissionById(permissionId);
        if (permission.getPermissionName().equals(request.permissionName())) {
            log.warn("Provide new permission to update permission: {} | {}", request.permissionName(), permission.getPermissionName());
            throw new DuplicatePermissionName("Permission not found");
        }
        permissionMapper.updatePermission(request, permission);
        log.info("Permission {} updated", permission.getPermissionName());
        return permissionMapper.toResponse(permission);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deletePermissionById(final Long permissionId) {
        final Permission permission = validateAndFindPermissionById(permissionId);
        permissionCrudRepository.delete(permission);
        log.info("Permission {} with ID {} deleted successfully.", permission.getPermissionName(), permissionId);
    }
}
