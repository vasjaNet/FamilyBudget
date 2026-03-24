package org.s3m.userservice.user.dto;

public record CreateUserRequest(
    String username,
    String email,
    String firstName,
    String lastName
) {}