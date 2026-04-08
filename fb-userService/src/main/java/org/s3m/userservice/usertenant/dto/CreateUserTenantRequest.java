package org.s3m.userservice.usertenant.dto;

import java.util.UUID;

public record CreateUserTenantRequest(
    UUID userId,
    UUID tenantId
) {}
