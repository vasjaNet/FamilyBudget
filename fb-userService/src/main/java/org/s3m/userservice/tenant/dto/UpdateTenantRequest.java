package org.s3m.userservice.tenant.dto;

import org.s3m.userservice.tenant.entity.TenantType;

public record UpdateTenantRequest(
    String name,
    String description,
    TenantType type
) {}
