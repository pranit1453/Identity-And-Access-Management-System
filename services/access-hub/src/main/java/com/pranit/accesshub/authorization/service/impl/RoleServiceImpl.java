package com.pranit.accesshub.authorization.service.impl;

import com.pranit.accesshub.authorization.constant.SortRole;
import com.pranit.accesshub.authorization.dto.CreateRoleRequest;
import com.pranit.accesshub.authorization.dto.RoleResponse;
import com.pranit.accesshub.authorization.dto.UpdateRoleRequest;
import com.pranit.accesshub.authorization.exception.DuplicateRoleName;
import com.pranit.accesshub.authorization.exception.RoleAlreadyAssociated;
import com.pranit.accesshub.authorization.exception.RoleAlreadyExists;
import com.pranit.accesshub.authorization.exception.RoleNotFoundException;
import com.pranit.accesshub.authorization.mapper.RoleMapper;
import com.pranit.accesshub.authorization.repository.RoleRepository;
import com.pranit.accesshub.authorization.repository.UserRoleRepository;
import com.pranit.accesshub.authorization.service.RoleService;
import com.pranit.accesshub.authorization.specification.RoleSpecification;
import com.pranit.accesshub.entities.entity.Role;
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
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleCrudRepository;
    private final RoleMapper roleMapper;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewRole(final CreateRoleRequest request) {
        if (roleCrudRepository.existsByRoleName(request.roleName())) {
            log.warn("Role {} already exists", request.roleName());
            throw new RoleAlreadyExists("Role already exists");
        }
        final Role role = roleMapper.toRole(request);
        final Role save = roleCrudRepository.save(role);
        log.info("Role {} created", save.getRoleName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int createNewRoles(final Set<CreateRoleRequest> request) {
        final Set<String> roleNames = request.stream()
                .map(CreateRoleRequest::roleName)
                .collect(Collectors.toUnmodifiableSet());
        final Set<String> existingRoleNames = roleCrudRepository.findAllByRoleNameIn(roleNames)
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.toUnmodifiableSet());
        final List<Role> rolesToSave = request.stream()
                .filter(req -> {
                    final boolean exists = existingRoleNames.contains(req.roleName());
                    if (exists) log.warn("Skipping role '{}' because it already exists.", req.roleName());
                    return !exists;
                }).map(roleMapper::toRole)
                .toList();
        if (rolesToSave.isEmpty()) {
            log.info("No new roles to create.");
            return 0;
        }
        roleCrudRepository.saveAll(rolesToSave);
        log.info("Created {} new roles.", rolesToSave.size());
        return rolesToSave.size();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public RoleResponse fetchRoleById(final Long roleId) {
        final Role role = validateAndFindRoleById(roleId);
        return roleMapper.toResponse(role);
    }

    private Role validateAndFindRoleById(final Long roleId) {
        log.debug("Validating and fetching role ID: {}", roleId);
        return roleCrudRepository.findByRoleId(roleId)
                .orElseThrow(() -> {
                    log.warn("Role {} not found", roleId);
                    return new RoleNotFoundException("Role not found");
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PageResponse<RoleResponse> fetchRoles(final int page, final int size, final String keyword, final SortRole sortBy, final String sortDirection) {
        final Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(Sort.Direction.ASC, sortBy.getField())
                : Sort.by(Sort.Direction.DESC, sortBy.getField());
        final Pageable pageable = PageRequest.of(page, size, sort);
        final Specification<Role> specification = RoleSpecification.searchKeyword(keyword);
        final Page<Role> pages = roleCrudRepository.findAll(specification, pageable);
        final List<RoleResponse> content = pages.getContent()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
        log.debug("Fetch {} roles", pages.getTotalElements());
        return PageResponse.<RoleResponse>builder()
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
    public RoleResponse updateRoleById(final UpdateRoleRequest request, final Long roleId) {
        final Role role = validateAndFindRoleById(roleId);
        if (role.getRoleName().equals(request.roleName())) {
            throw new DuplicateRoleName("Role not found");
        }
        roleMapper.updateRole(request, role);
        log.info("Role {} updated", role.getRoleName());
        return roleMapper.toResponse(role);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRoleById(final Long roleId) {
        final Role role = validateAndFindRoleById(roleId);
        if (userRoleRepository.existsByRole_RoleId(roleId)) {
            throw new RoleAlreadyAssociated("Role cannot be deleted because it is associated with one or more users.");
        }
        roleCrudRepository.delete(role);
        log.info("Role '{}' with ID {} deleted successfully.", role.getRoleName(), roleId);
    }

}
