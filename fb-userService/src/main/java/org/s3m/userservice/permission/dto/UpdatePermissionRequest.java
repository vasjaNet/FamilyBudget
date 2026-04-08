package org.s3m.userservice.permission.dto;

public record UpdatePermissionRequest(
    String name,
    String resource,
    String action,
    String description
) {
}