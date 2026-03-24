package org.s3m.userservice.family.dto;

import org.s3m.userservice.family.entity.FamilyMember;

import java.time.LocalDateTime;
import java.util.UUID;

public record FamilyMemberResponse(
    UUID id,
    UUID familyId,
    UUID userId,
    String username,
    String email,
    FamilyMember.FamilyRole role,
    LocalDateTime createdAt,
    String createdBy
) {}