package org.s3m.userservice.usertenant.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserTenantResponse(
    UUID id,
    UUID userId,
    String userUsername,
    UUID tenantId,
    String tenantName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {}
