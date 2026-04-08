package org.s3m.userservice.tenant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.s3m.userservice.tenant.dto.CreateTenantRequest;
import org.s3m.userservice.tenant.dto.TenantResponse;
import org.s3m.userservice.tenant.dto.UpdateTenantRequest;
import org.s3m.userservice.tenant.entity.Tenant;

@Mapper
public interface TenantMapper {

    TenantResponse mapToResponse(Tenant tenant);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Tenant mapToEntity(CreateTenantRequest request);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void mapToEntity(UpdateTenantRequest request, @MappingTarget Tenant tenant);

}
