package org.s3m.userservice.role.dto;

public record CreateRoleRequest(
    String name,
    String description
) {
}
