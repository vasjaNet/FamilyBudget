package org.s3m.userservice.family.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FamilyResponse(
    UUID id,
    String name,
    String description,
    UUID ownerId,
    String ownerUsername,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy,
    int memberCount
) {}