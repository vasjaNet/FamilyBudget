package org.s3m.userservice.family.dto;

import org.s3m.userservice.family.entity.FamilyMember;

import java.util.UUID;

public record AddMemberRequest(
    UUID userId,
    FamilyMember.FamilyRole role
) {}