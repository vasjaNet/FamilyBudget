package org.s3m.userservice.user.dto;

import org.s3m.userservice.user.entity.UserStatus;

import java.util.UUID;

public record UserResponseBasic(
    UUID id,
    String username,
    String email,
    String fullName,
    UserStatus status
) {}
