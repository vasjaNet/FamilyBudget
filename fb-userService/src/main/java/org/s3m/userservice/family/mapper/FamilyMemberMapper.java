package org.s3m.userservice.family.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.s3m.userservice.family.dto.FamilyMemberResponse;
import org.s3m.userservice.family.entity.FamilyMember;

@Mapper
public interface FamilyMemberMapper {

    @Mapping(target = "familyId", ignore = true)
    FamilyMemberResponse mapToResponse(FamilyMember member, String username, String email);

}