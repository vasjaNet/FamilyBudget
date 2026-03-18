package org.s3m.userservice.family.dto;

import lombok.*;
import org.s3m.userservice.family.entity.FamilyMember;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMemberRequest {
    private UUID userId;
    private FamilyMember.FamilyRole role;
}