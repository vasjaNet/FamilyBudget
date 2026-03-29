package org.s3m.userservice.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.s3m.userservice.user.dto.CreateUserRequest;
import org.s3m.userservice.user.dto.UserResponse;
import org.s3m.userservice.user.entity.User;

@Mapper
public interface UserMapper {

    UserResponse mapToResponse(User user);

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User mapToUser(CreateUserRequest request);

    User mapToUser(UserResponse response);

}
