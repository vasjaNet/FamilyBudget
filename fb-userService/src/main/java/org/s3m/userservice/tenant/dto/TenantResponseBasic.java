package org.s3m.userservice.tenant.dto;

import org.s3m.userservice.tenant.entity.TenantType;

import java.util.UUID;

public record TenantResponseBasic(
    UUID id,
    String name,
    String description,
    TenantType type
) {}
