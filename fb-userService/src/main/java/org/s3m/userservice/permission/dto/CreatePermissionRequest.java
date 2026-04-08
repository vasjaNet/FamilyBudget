package org.s3m.userservice.permission.dto;

public record CreatePermissionRequest(
    String name,
    String resource,
    String action,
    String description
) {
}