package org.s3m.userservice.role.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.s3m.userservice.role.dto.CreateRoleRequest;
import org.s3m.userservice.role.dto.RoleResponse;
import org.s3m.userservice.role.dto.RoleResponseBasic;
import org.s3m.userservice.role.dto.UpdateRoleRequest;
import org.s3m.userservice.role.entity.Role;

import java.util.List;

@Mapper
public interface RoleMapper {

    RoleResponse mapToResponse(Role role);
    List<RoleResponse> mapToResponseList(List<Role> role);

    RoleResponseBasic mapToResponseBasic(Role role);
    List<RoleResponseBasic> mapToResponseBasicList(List<Role> role);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Role mapToEntity(CreateRoleRequest request);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void mapToEntity(UpdateRoleRequest request, @MappingTarget Role role);

}
