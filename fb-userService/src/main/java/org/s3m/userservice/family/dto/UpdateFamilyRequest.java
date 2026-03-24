package org.s3m.userservice.family.dto;

public record UpdateFamilyRequest(
    String name,
    String description
) {}