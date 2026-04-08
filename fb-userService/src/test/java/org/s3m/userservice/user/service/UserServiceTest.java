package org.s3m.userservice.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.s3m.userservice.user.dto.CreateUserRequest;
import org.s3m.userservice.user.dto.UpdateUserRequest;
import org.s3m.userservice.user.dto.UserResponse;
import org.s3m.userservice.user.entity.User;
import org.s3m.userservice.user.entity.UserStatus;
import org.s3m.userservice.user.mapper.UserMapper;
import org.s3m.userservice.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link UserService}
 * Tests business logic in isolation using Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UUID testUserId;
    private User testUser;
    private UserResponse testUserResponse;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testUser = User.builder()
            .id(testUserId)
            .username("testuser")
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .createdBy("system")
            .build();

        testUserResponse = new UserResponse(
            testUserId,
            "testuser",
            "test@example.com",
            "John",
            "Doe",
            UserStatus.ACTIVE,
            testUser.getCreatedAt(),
            testUser.getUpdatedAt(),
            "system",
            null
        );

        createUserRequest = new CreateUserRequest(
            "newuser",
            "newuser@example.com",
            "Jane",
            "Smith",
            UserStatus.ACTIVE
        );

        updateUserRequest = new UpdateUserRequest(
            "updated@example.com",
            "John",
            "Updated"
                ,UserStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        User newUser = User.builder()
            .username("newuser")
            .email("newuser@example.com")
            .firstName("Jane")
            .lastName("Smith")
            .status(UserStatus.ACTIVE)
            .build();

        User savedUser = User.builder()
            .id(UUID.randomUUID())
            .username("newuser")
            .email("newuser@example.com")
            .firstName("Jane")
            .lastName("Smith")
            .status(UserStatus.ACTIVE)
            .createdBy("admin")
            .build();

        UserResponse expectedResponse = new UserResponse(
            savedUser.getId(),
            "newuser",
            "newuser@example.com",
            "Jane",
            "Smith",
            UserStatus.ACTIVE,
            null,
            null,
            "admin",
            null
        );

        given(userRepository.existsByUsername("newuser")).willReturn(false);
        given(userRepository.existsByEmail("newuser@example.com")).willReturn(false);
        given(userMapper.mapToUser(createUserRequest)).willReturn(newUser);
        given(userRepository.save(newUser)).willReturn(savedUser);
        given(userMapper.mapToResponse(savedUser)).willReturn(expectedResponse);

        // When
        UserResponse result = userService.createUser(createUserRequest, "admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("newuser");
        assertThat(result.email()).isEqualTo("newuser@example.com");
        verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing username")
    void shouldThrowExceptionWhenCreatingUserWithExistingUsername() {
        // Given
        given(userRepository.existsByUsername("newuser")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(createUserRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing email")
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        // Given
        given(userRepository.existsByUsername("newuser")).willReturn(false);
        given(userRepository.existsByEmail("newuser@example.com")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(createUserRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() {
        // Given
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        given(userMapper.mapToResponse(testUser)).willReturn(testUserResponse);

        // When
        UserResponse result = userService.getUserById(testUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testUserId);
        assertThat(result.username()).isEqualTo("testuser");
        verify(userRepository).findById(testUserId);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Given
        given(userRepository.findById(testUserId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(testUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void shouldGetUserByUsernameSuccessfully() {
        // Given
        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(testUser));
        given(userMapper.mapToResponse(testUser)).willReturn(testUserResponse);

        // When
        UserResponse result = userService.getUserByUsername("testuser");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found by username")
    void shouldThrowExceptionWhenUserNotFoundByUsername() {
        // Given
        given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserByUsername("unknown"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() {
        // Given
        User user2 = User.builder()
            .id(UUID.randomUUID())
            .username("user2")
            .email("user2@example.com")
            .firstName("Jane")
            .lastName("Doe")
            .status(UserStatus.ACTIVE)
            .build();

        UserResponse userResponse2 = new UserResponse(
            user2.getId(),
            "user2",
            "user2@example.com",
            "Jane",
            "Doe",
            UserStatus.ACTIVE,
            null,
            null,
            null,
            null
        );

        given(userRepository.findAll()).willReturn(List.of(testUser, user2));
        given(userMapper.mapToResponse(testUser)).willReturn(testUserResponse);
        given(userMapper.mapToResponse(user2)).willReturn(userResponse2);

        // When
        List<UserResponse> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).username()).isEqualTo("testuser");
        assertThat(result.get(1).username()).isEqualTo("user2");
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsersExist() {
        // Given
        given(userRepository.findAll()).willReturn(List.of());

        // When
        List<UserResponse> result = userService.getAllUsers();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        User updatedUser = User.builder()
            .id(testUserId)
            .username("testuser")
            .email("updated@example.com")
            .firstName("John")
            .lastName("Updated")
            .status(UserStatus.ACTIVE)
            .createdBy("system")
            .updatedBy("admin")
            .build();

        UserResponse updatedResponse = new UserResponse(
            testUserId,
            "testuser",
            "updated@example.com",
            "John",
            "Updated",
            UserStatus.ACTIVE,
            testUser.getCreatedAt(),
            LocalDateTime.now(),
            "system",
            "admin"
        );

        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        given(userRepository.existsByEmail("updated@example.com")).willReturn(false);
        given(userRepository.save(testUser)).willReturn(updatedUser);
        given(userMapper.mapToResponse(updatedUser)).willReturn(updatedResponse);

        // When
        UserResponse result = userService.updateUser(testUserId, updateUserRequest, "admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("updated@example.com");
        assertThat(result.lastName()).isEqualTo("Updated");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when updating user with existing email")
    void shouldThrowExceptionWhenUpdatingWithExistingEmail() {
        // Given
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        given(userRepository.existsByEmail("updated@example.com")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(testUserId, updateUserRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        given(userRepository.findById(testUserId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(testUserId, updateUserRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));

        // When
        userService.deleteUser(testUserId, "admin");

        // Then
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        given(userRepository.findById(testUserId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(testUserId, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");

        verify(userRepository, never()).delete(any());
    }
}
