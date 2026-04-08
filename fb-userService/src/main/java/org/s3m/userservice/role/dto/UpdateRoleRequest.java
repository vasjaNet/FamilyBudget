package org.s3m.userservice.role.dto;

public record UpdateRoleRequest(
    String name,
    String description
) {
}
