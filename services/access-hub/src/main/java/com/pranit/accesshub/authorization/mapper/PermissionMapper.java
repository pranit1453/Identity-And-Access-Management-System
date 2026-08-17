package com.pranit.accesshub.authorization.mapper;


import com.pranit.accesshub.authorization.dto.CreatePermissionRequest;
import com.pranit.accesshub.authorization.dto.PermissionResponse;
import com.pranit.accesshub.authorization.dto.UpdatePermissionRequest;
import com.pranit.accesshub.entities.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "permissionId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Permission toPermission(CreatePermissionRequest request);

    PermissionResponse toResponse(Permission permission);

    @Mapping(target = "permissionId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updatePermission(UpdatePermissionRequest request, @MappingTarget Permission permission);
}
