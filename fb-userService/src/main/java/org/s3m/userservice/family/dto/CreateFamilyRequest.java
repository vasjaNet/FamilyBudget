package org.s3m.userservice.family.dto;

public record CreateFamilyRequest(
    String name,
    String description
) {}