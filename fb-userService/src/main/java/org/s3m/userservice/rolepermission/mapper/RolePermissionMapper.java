package org.s3m.userservice.rolepermission.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.s3m.userservice.rolepermission.dto.CreateRolePermissionRequest;
import org.s3m.userservice.rolepermission.dto.RolePermissionResponse;
import org.s3m.userservice.rolepermission.entity.RolePermission;

@Mapper
public interface RolePermissionMapper {

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "permissionId", source = "permission.id")
    @Mapping(target = "permissionName", source = "permission.name")
    @Mapping(target = "permissionResource", source = "permission.resource")
    @Mapping(target = "permissionAction", source = "permission.action")
    RolePermissionResponse mapToResponse(RolePermission rolePermission);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "permission", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RolePermission mapToEntity(CreateRolePermissionRequest request);

}