package org.s3m.userservice.family.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.s3m.userservice.family.dto.CreateFamilyRequest;
import org.s3m.userservice.family.dto.FamilyResponse;
import org.s3m.userservice.family.dto.UpdateFamilyRequest;
import org.s3m.userservice.family.entity.Family;

@Mapper
public interface FamilyMapper {

    FamilyResponse mapToResponse(Family family, String ownerUsername, int memberCount);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "familyMembers", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Family mapToEntity(CreateFamilyRequest request);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "familyMembers", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void mapToEntity(UpdateFamilyRequest request, @MappingTarget Family family);

}