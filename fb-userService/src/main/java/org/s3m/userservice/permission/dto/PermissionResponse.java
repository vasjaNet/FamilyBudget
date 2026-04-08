package org.s3m.userservice.permission.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PermissionResponse(
    UUID id,
    String name,
    String resource,
    String action,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {
}