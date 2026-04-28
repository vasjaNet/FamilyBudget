package org.s3m.userservice.role.dto;

import java.util.UUID;

public record RoleResponseBasic(
    UUID id,
    String name,
    String description
) {
}
