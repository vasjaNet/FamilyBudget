package org.s3m.userservice.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.s3m.userservice.user.dto.*;
import org.s3m.userservice.user.entity.User;
import org.s3m.userservice.usertenant.entity.UserTenant;

import java.util.List;

@Mapper
public interface UserMapper {


    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "tenantName", source = "tenant.name")
    @Mapping(target = "roleId", source = "userRole.id")
    @Mapping(target = "roleName", source = "userRole.name")
    UserTenantResponse mapToResponse(UserTenant userTenant);

    UserResponse mapToResponse(User user);

    List<UserResponse> mapToResponseList(List<User> users);

    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserResponseBasic mapToUserResponseBasic(User user);

    List<UserResponseBasic> mapToUserResponseBasicList(List<User> user);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userTenants", ignore = true)
    User mapToUser(CreateUserRequest request);

    @Mapping(target = "userTenants", ignore = true)
    User mapToUser(UserResponse response);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userTenants", ignore = true)
    void updateUserFromRequest(UpdateUserRequest request, @MappingTarget User user);

}
