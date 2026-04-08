package org.s3m.userservice.rolepermission.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RolePermissionResponse(
    UUID id,
    UUID roleId,
    String roleName,
    UUID permissionId,
    String permissionName,
    String permissionResource,
    String permissionAction,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {
}