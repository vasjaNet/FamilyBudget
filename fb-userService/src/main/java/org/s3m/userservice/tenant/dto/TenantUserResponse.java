package org.s3m.userservice.tenant.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TenantUserResponse(
    UUID id,
    UUID userId,
    String userUsername,
    UUID roleId,
    String roleName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {}
