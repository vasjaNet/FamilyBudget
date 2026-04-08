package org.s3m.userservice.rolepermission.dto;

import java.util.UUID;

public record CreateRolePermissionRequest(
    UUID roleId,
    UUID permissionId
) {
}