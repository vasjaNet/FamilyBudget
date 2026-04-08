package org.s3m.userservice.user.dto;

import org.s3m.userservice.user.entity.UserStatus;

public record UpdateUserRequest(
    String email,
    String firstName,
    String lastName,
    UserStatus status
) {}
