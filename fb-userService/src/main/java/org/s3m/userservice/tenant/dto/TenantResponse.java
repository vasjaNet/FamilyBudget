package org.s3m.userservice.tenant.dto;

import org.s3m.userservice.tenant.entity.TenantType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TenantResponse(
    UUID id,
    String name,
    String description,
    TenantType type,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy,
    List<TenantUserResponse> userTenants
) {}
