package org.s3m.userservice.role.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoleResponse(
    UUID id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {
}
