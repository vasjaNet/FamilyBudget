package org.s3m.userservice.user.dto;

import org.s3m.userservice.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    String firstName,
    String lastName,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy,
    List<UserTenantResponse> userTenants
) {}
