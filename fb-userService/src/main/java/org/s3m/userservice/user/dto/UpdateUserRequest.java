package org.s3m.userservice.user.dto;

public record UpdateUserRequest(
    String email,
    String firstName,
    String lastName
) {}