package org.s3m.userservice.role.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.s3m.userservice.role.dto.CreateRoleRequest;
import org.s3m.userservice.role.dto.RoleResponse;
import org.s3m.userservice.role.dto.UpdateRoleRequest;
import org.s3m.userservice.role.entity.Role;
import org.s3m.userservice.role.mapper.RoleMapper;
import org.s3m.userservice.role.repository.RoleRepository;

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
 * Unit tests for {@link RoleService}
 * Tests business logic in isolation using Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Role Service Tests")
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private UUID testRoleId;
    private Role testRole;
    private RoleResponse testRoleResponse;
    private CreateRoleRequest createRoleRequest;
    private UpdateRoleRequest updateRoleRequest;

    @BeforeEach
    void setUp() {
        testRoleId = UUID.randomUUID();

        testRole = Role.builder()
            .id(testRoleId)
            .name("ADMIN")
            .description("Administrator role with full access")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .createdBy("system")
            .updatedBy("system")
            .build();

        testRoleResponse = new RoleResponse(
            testRoleId,
            "ADMIN",
            "Administrator role with full access",
            testRole.getCreatedAt(),
            testRole.getUpdatedAt(),
            "system",
            "system"
        );

        createRoleRequest = new CreateRoleRequest(
            "USER",
            "Standard user role with limited access"
        );

        updateRoleRequest = new UpdateRoleRequest(
            "SUPER_ADMIN",
            "Super administrator role"
        );
    }

    @Test
    @DisplayName("Should create role successfully")
    void shouldCreateRoleSuccessfully() {
        // Given
        Role roleToSave = Role.builder()
            .name("USER")
            .description("Standard user role with limited access")
            .build();

        Role savedRole = Role.builder()
            .id(UUID.randomUUID())
            .name("USER")
            .description("Standard user role with limited access")
            .createdBy("admin")
            .updatedBy("admin")
            .build();

        given(roleRepository.existsByName("USER")).willReturn(false);
        given(roleMapper.mapToEntity(createRoleRequest)).willReturn(roleToSave);
        given(roleRepository.save(roleToSave)).willReturn(savedRole);
        given(roleMapper.mapToResponse(savedRole)).willReturn(
            new RoleResponse(savedRole.getId(), "USER", "Standard user role with limited access",
                null, null, "admin", "admin")
        );

        // When
        RoleResponse result = roleService.createRole(createRoleRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("USER");
        assertThat(result.description()).isEqualTo("Standard user role with limited access");
        verify(roleRepository).save(roleToSave);
    }

    @Test
    @DisplayName("Should throw exception when creating role with existing name")
    void shouldThrowExceptionWhenCreatingRoleWithExistingName() {
        // Given
        given(roleRepository.existsByName("USER")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> roleService.createRole(createRoleRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Role with name 'USER' already exists");

        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get role by ID successfully")
    void shouldGetRoleByIdSuccessfully() {
        // Given
        given(roleRepository.findById(testRoleId)).willReturn(Optional.of(testRole));
        given(roleMapper.mapToResponse(testRole)).willReturn(testRoleResponse);

        // When
        RoleResponse result = roleService.getRoleById(testRoleId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testRoleId);
        assertThat(result.name()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should throw exception when role not found by ID")
    void shouldThrowExceptionWhenRoleNotFoundById() {
        // Given
        given(roleRepository.findById(testRoleId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roleService.getRoleById(testRoleId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Role not found with id: " + testRoleId);
    }

    @Test
    @DisplayName("Should get role by name successfully")
    void shouldGetRoleByNameSuccessfully() {
        // Given
        given(roleRepository.findByName("ADMIN")).willReturn(Optional.of(testRole));
        given(roleMapper.mapToResponse(testRole)).willReturn(testRoleResponse);

        // When
        RoleResponse result = roleService.getRoleByName("ADMIN");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should throw exception when role not found by name")
    void shouldThrowExceptionWhenRoleNotFoundByName() {
        // Given
        given(roleRepository.findByName("UNKNOWN")).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roleService.getRoleByName("UNKNOWN"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Role not found with name: UNKNOWN");
    }

    @Test
    @DisplayName("Should get all roles")
    void shouldGetAllRoles() {
        // Given
        Role role2 = Role.builder()
            .id(UUID.randomUUID())
            .name("USER")
            .description("User role")
            .build();

        given(roleRepository.findAll()).willReturn(List.of(testRole, role2));
        given(roleMapper.mapToResponse(testRole)).willReturn(testRoleResponse);
        given(roleMapper.mapToResponse(role2)).willReturn(
            new RoleResponse(role2.getId(), "USER", "User role", null, null, null, null)
        );

        // When
        List<RoleResponse> result = roleService.getAllRoles();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("ADMIN");
        assertThat(result.get(1).name()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should return empty list when no roles exist")
    void shouldReturnEmptyListWhenNoRolesExist() {
        // Given
        given(roleRepository.findAll()).willReturn(List.of());

        // When
        List<RoleResponse> result = roleService.getAllRoles();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should update role successfully")
    void shouldUpdateRoleSuccessfully() {
        // Given
        Role updatedRole = Role.builder()
            .id(testRoleId)
            .name("SUPER_ADMIN")
            .description("Super administrator role")
            .createdBy("system")
            .updatedBy("admin")
            .build();

        given(roleRepository.findById(testRoleId)).willReturn(Optional.of(testRole));
        given(roleRepository.existsByName("SUPER_ADMIN")).willReturn(false);
        given(roleRepository.save(testRole)).willReturn(updatedRole);
        given(roleMapper.mapToResponse(updatedRole)).willReturn(
            new RoleResponse(testRoleId, "SUPER_ADMIN", "Super administrator role",
                null, null, "system", "admin")
        );

        // When
        RoleResponse result = roleService.updateRole(testRoleId, updateRoleRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("SUPER_ADMIN");
        assertThat(result.description()).isEqualTo("Super administrator role");
    }

    @Test
    @DisplayName("Should throw exception when updating role with existing name")
    void shouldThrowExceptionWhenUpdatingWithExistingName() {
        // Given
        given(roleRepository.findById(testRoleId)).willReturn(Optional.of(testRole));
        given(roleRepository.existsByName("SUPER_ADMIN")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> roleService.updateRole(testRoleId, updateRoleRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Role with name 'SUPER_ADMIN' already exists");
    }

    @Test
    @DisplayName("Should allow updating role when name is unchanged")
    void shouldAllowUpdateWhenNameUnchanged() {
        // Given
        UpdateRoleRequest sameNameRequest = new UpdateRoleRequest("ADMIN", "Updated description");

        Role updatedRole = Role.builder()
            .id(testRoleId)
            .name("ADMIN")
            .description("Updated description")
            .build();

        given(roleRepository.findById(testRoleId)).willReturn(Optional.of(testRole));
        given(roleRepository.save(testRole)).willReturn(updatedRole);
        given(roleMapper.mapToResponse(updatedRole)).willReturn(
            new RoleResponse(testRoleId, "ADMIN", "Updated description", null, null, null, "admin")
        );

        // When
        RoleResponse result = roleService.updateRole(testRoleId, sameNameRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("ADMIN");
        verify(roleRepository).save(testRole);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent role")
    void shouldThrowExceptionWhenUpdatingNonExistentRole() {
        // Given
        given(roleRepository.findById(testRoleId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roleService.updateRole(testRoleId, updateRoleRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Role not found with id: " + testRoleId);
    }

    @Test
    @DisplayName("Should delete role successfully")
    void shouldDeleteRoleSuccessfully() {
        // Given
        given(roleRepository.findById(testRoleId)).willReturn(Optional.of(testRole));

        // When
        roleService.deleteRole(testRoleId, "admin");

        // Then
        verify(roleRepository).delete(testRole);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent role")
    void shouldThrowExceptionWhenDeletingNonExistentRole() {
        // Given
        given(roleRepository.findById(testRoleId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roleService.deleteRole(testRoleId, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Role not found with id: " + testRoleId);

        verify(roleRepository, never()).delete(any());
    }
}
