package org.s3m.userservice.family.dto;

import lombok.*;
import org.s3m.userservice.family.entity.FamilyMember;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMemberResponse {
    private UUID id;
    private UUID familyId;
    private UUID userId;
    private String username;
    private String email;
    private FamilyMember.FamilyRole role;
    private LocalDateTime createdAt;
    private String createdBy;
}