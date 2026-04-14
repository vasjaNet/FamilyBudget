package org.s3m.userservice.rolepermission.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RolePermissionResponse(
    UUID roleId,
    UUID permissionId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {
}