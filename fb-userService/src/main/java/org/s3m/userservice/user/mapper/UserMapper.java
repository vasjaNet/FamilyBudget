package org.s3m.userservice.user.mapper;

import org.mapstruct.Mapper;
import org.s3m.userservice.user.dto.CreateUserRequest;
import org.s3m.userservice.user.dto.UserResponse;
import org.s3m.userservice.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse mapToResponse(User user);

    User mapToUser(CreateUserRequest request);

    User mapToUser(UserResponse response);

}
