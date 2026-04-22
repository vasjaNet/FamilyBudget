package org.s3m.userservice.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserTenantResponse(
    UUID id,
    UUID tenantId,
    String tenantName,
    UUID roleId,
    String roleName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {}
