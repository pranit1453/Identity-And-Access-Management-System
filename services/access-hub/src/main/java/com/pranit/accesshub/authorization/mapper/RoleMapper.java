package com.pranit.accesshub.authorization.mapper;

import com.pranit.accesshub.authorization.dto.CreateRoleRequest;
import com.pranit.accesshub.authorization.dto.RoleResponse;
import com.pranit.accesshub.authorization.dto.UpdateRoleRequest;
import com.pranit.accesshub.entities.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Role toRole(CreateRoleRequest request);

    RoleResponse toResponse(Role role);

    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateRole(UpdateRoleRequest request, @MappingTarget Role role);
}
