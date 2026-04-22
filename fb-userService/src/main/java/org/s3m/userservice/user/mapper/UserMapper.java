package org.s3m.userservice.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.s3m.userservice.user.dto.CreateUserRequest;
import org.s3m.userservice.user.dto.UpdateUserRequest;
import org.s3m.userservice.user.dto.UserResponse;
import org.s3m.userservice.user.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {

    UserResponse mapToResponse(User user);

    List<UserResponse> mapToResponseList(List<User> users);

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
