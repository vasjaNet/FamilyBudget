package org.s3m.userservice.family.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.s3m.userservice.family.dto.CreateFamilyRequest;
import org.s3m.userservice.family.dto.FamilyResponse;
import org.s3m.userservice.family.dto.UpdateFamilyRequest;
import org.s3m.userservice.family.entity.Family;

@Mapper
public interface FamilyMapper {

    FamilyResponse mapToResponse(Family family);

    Family mapToEntity(CreateFamilyRequest request);

    void mapToEntity(UpdateFamilyRequest request, @MappingTarget Family family);

}