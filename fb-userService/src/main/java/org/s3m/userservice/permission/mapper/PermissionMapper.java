package org.s3m.userservice.permission.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.s3m.userservice.permission.dto.CreatePermissionRequest;
import org.s3m.userservice.permission.dto.PermissionResponse;
import org.s3m.userservice.permission.dto.UpdatePermissionRequest;
import org.s3m.userservice.permission.entity.Permission;

@Mapper
public interface PermissionMapper {

    PermissionResponse mapToResponse(Permission permission);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Permission mapToEntity(CreatePermissionRequest request);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void mapToEntity(UpdatePermissionRequest request, @MappingTarget Permission permission);

}