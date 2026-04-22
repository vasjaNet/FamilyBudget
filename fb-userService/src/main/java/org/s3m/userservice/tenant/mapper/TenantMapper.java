package org.s3m.userservice.tenant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.s3m.userservice.tenant.dto.*;
import org.s3m.userservice.tenant.entity.Tenant;
import org.s3m.userservice.usertenant.entity.UserTenant;

@Mapper
public interface TenantMapper {


    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userUsername", source = "user.username")
    @Mapping(target = "roleId", source = "userRole.id")
    @Mapping(target = "roleName", source = "userRole.name")
    TenantUserResponse mapToResponse(UserTenant userTenant);

    TenantResponse mapToResponse(Tenant tenant);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userTenants", ignore = true)
    Tenant mapToEntity(CreateTenantRequest request);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userTenants", ignore = true)
    void mapToEntity(UpdateTenantRequest request, @MappingTarget Tenant tenant);

}
