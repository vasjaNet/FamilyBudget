package org.s3m.userservice.permission.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.s3m.userservice.permission.dto.CreatePermissionRequest;
import org.s3m.userservice.permission.dto.PermissionResponse;
import org.s3m.userservice.permission.dto.UpdatePermissionRequest;
import org.s3m.userservice.permission.entity.Permission;
import org.s3m.userservice.permission.mapper.PermissionMapper;
import org.s3m.userservice.permission.repository.PermissionRepository;

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
 * Unit tests for {@link PermissionService}
 * Tests business logic in isolation using Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Permission Service Tests")
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionService permissionService;

    private UUID testPermissionId;
    private Permission testPermission;
    private PermissionResponse testPermissionResponse;
    private CreatePermissionRequest createPermissionRequest;
    private UpdatePermissionRequest updatePermissionRequest;

    @BeforeEach
    void setUp() {
        testPermissionId = UUID.randomUUID();

        testPermission = Permission.builder()
            .id(testPermissionId)
            .name("USER_READ")
            .resource("USER")
            .action("READ")
            .description("Can read user data")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .createdBy("system")
            .updatedBy("system")
            .build();

        testPermissionResponse = new PermissionResponse(
            testPermissionId,
            "USER_READ",
            "USER",
            "READ",
            "Can read user data",
            testPermission.getCreatedAt(),
            testPermission.getUpdatedAt(),
            "system",
            "system"
        );

        createPermissionRequest = new CreatePermissionRequest(
            "USER_WRITE",
            "USER",
            "WRITE",
            "Can write user data"
        );

        updatePermissionRequest = new UpdatePermissionRequest(
            "USER_READ_UPDATED",
            "USER",
            "READ",
            "Updated description"
        );
    }

    @Test
    @DisplayName("Should create permission successfully")
    void shouldCreatePermissionSuccessfully() {
        // Given
        Permission permissionToSave = Permission.builder()
            .name("USER_WRITE")
            .resource("USER")
            .action("WRITE")
            .description("Can write user data")
            .build();

        Permission savedPermission = Permission.builder()
            .id(UUID.randomUUID())
            .name("USER_WRITE")
            .resource("USER")
            .action("WRITE")
            .description("Can write user data")
            .createdBy("admin")
            .updatedBy("admin")
            .build();

        given(permissionRepository.existsByName("USER_WRITE")).willReturn(false);
        given(permissionMapper.mapToEntity(createPermissionRequest)).willReturn(permissionToSave);
        given(permissionRepository.save(permissionToSave)).willReturn(savedPermission);
        given(permissionMapper.mapToResponse(savedPermission)).willReturn(
            new PermissionResponse(savedPermission.getId(), "USER_WRITE", "USER", "WRITE",
                "Can write user data", null, null, "admin", "admin")
        );

        // When
        PermissionResponse result = permissionService.createPermission(createPermissionRequest, "admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("USER_WRITE");
        assertThat(result.resource()).isEqualTo("USER");
        assertThat(result.action()).isEqualTo("WRITE");
        verify(permissionRepository).save(permissionToSave);
    }

    @Test
    @DisplayName("Should throw exception when creating permission with existing name")
    void shouldThrowExceptionWhenCreatingPermissionWithExistingName() {
        // Given
        given(permissionRepository.existsByName("USER_WRITE")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> permissionService.createPermission(createPermissionRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Permission with name 'USER_WRITE' already exists");

        verify(permissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get permission by ID successfully")
    void shouldGetPermissionByIdSuccessfully() {
        // Given
        given(permissionRepository.findById(testPermissionId)).willReturn(Optional.of(testPermission));
        given(permissionMapper.mapToResponse(testPermission)).willReturn(testPermissionResponse);

        // When
        PermissionResponse result = permissionService.getPermissionById(testPermissionId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testPermissionId);
        assertThat(result.name()).isEqualTo("USER_READ");
    }

    @Test
    @DisplayName("Should throw exception when permission not found by ID")
    void shouldThrowExceptionWhenPermissionNotFoundById() {
        // Given
        given(permissionRepository.findById(testPermissionId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> permissionService.getPermissionById(testPermissionId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Permission not found with id: " + testPermissionId);
    }

    @Test
    @DisplayName("Should get permission by name successfully")
    void shouldGetPermissionByNameSuccessfully() {
        // Given
        given(permissionRepository.findByName("USER_READ")).willReturn(Optional.of(testPermission));
        given(permissionMapper.mapToResponse(testPermission)).willReturn(testPermissionResponse);

        // When
        PermissionResponse result = permissionService.getPermissionByName("USER_READ");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("USER_READ");
    }

    @Test
    @DisplayName("Should throw exception when permission not found by name")
    void shouldThrowExceptionWhenPermissionNotFoundByName() {
        // Given
        given(permissionRepository.findByName("UNKNOWN")).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> permissionService.getPermissionByName("UNKNOWN"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Permission not found with name: UNKNOWN");
    }

    @Test
    @DisplayName("Should get all permissions")
    void shouldGetAllPermissions() {
        // Given
        Permission permission2 = Permission.builder()
            .id(UUID.randomUUID())
            .name("USER_WRITE")
            .resource("USER")
            .action("WRITE")
            .build();

        given(permissionRepository.findAll()).willReturn(List.of(testPermission, permission2));
        given(permissionMapper.mapToResponse(testPermission)).willReturn(testPermissionResponse);
        given(permissionMapper.mapToResponse(permission2)).willReturn(
            new PermissionResponse(permission2.getId(), "USER_WRITE", "USER", "WRITE",
                null, null, null, null, null)
        );

        // When
        List<PermissionResponse> result = permissionService.getAllPermissions();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("USER_READ");
        assertThat(result.get(1).name()).isEqualTo("USER_WRITE");
    }

    @Test
    @DisplayName("Should return empty list when no permissions exist")
    void shouldReturnEmptyListWhenNoPermissionsExist() {
        // Given
        given(permissionRepository.findAll()).willReturn(List.of());

        // When
        List<PermissionResponse> result = permissionService.getAllPermissions();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should get permissions by resource")
    void shouldGetPermissionsByResource() {
        // Given
        given(permissionRepository.findByResource("USER")).willReturn(List.of(testPermission));
        given(permissionMapper.mapToResponse(testPermission)).willReturn(testPermissionResponse);

        // When
        List<PermissionResponse> result = permissionService.getPermissionsByResource("USER");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).resource()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should get permissions by action")
    void shouldGetPermissionsByAction() {
        // Given
        given(permissionRepository.findByAction("READ")).willReturn(List.of(testPermission));
        given(permissionMapper.mapToResponse(testPermission)).willReturn(testPermissionResponse);

        // When
        List<PermissionResponse> result = permissionService.getPermissionsByAction("READ");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).action()).isEqualTo("READ");
    }

    @Test
    @DisplayName("Should update permission successfully")
    void shouldUpdatePermissionSuccessfully() {
        // Given
        Permission updatedPermission = Permission.builder()
            .id(testPermissionId)
            .name("USER_READ_UPDATED")
            .resource("USER")
            .action("READ")
            .description("Updated description")
            .createdBy("system")
            .updatedBy("admin")
            .build();

        given(permissionRepository.findById(testPermissionId)).willReturn(Optional.of(testPermission));
        given(permissionRepository.existsByName("USER_READ_UPDATED")).willReturn(false);
        given(permissionRepository.save(testPermission)).willReturn(updatedPermission);
        given(permissionMapper.mapToResponse(updatedPermission)).willReturn(
            new PermissionResponse(testPermissionId, "USER_READ_UPDATED", "USER", "READ",
                "Updated description", null, null, "system", "admin")
        );

        // When
        PermissionResponse result = permissionService.updatePermission(testPermissionId, updatePermissionRequest, "admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("USER_READ_UPDATED");
        assertThat(result.description()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("Should throw exception when updating permission with existing name")
    void shouldThrowExceptionWhenUpdatingWithExistingName() {
        // Given
        given(permissionRepository.findById(testPermissionId)).willReturn(Optional.of(testPermission));
        given(permissionRepository.existsByName("USER_READ_UPDATED")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> permissionService.updatePermission(testPermissionId, updatePermissionRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Permission with name 'USER_READ_UPDATED' already exists");
    }

    @Test
    @DisplayName("Should allow updating permission when name is unchanged")
    void shouldAllowUpdateWhenNameUnchanged() {
        // Given
        UpdatePermissionRequest sameNameRequest = new UpdatePermissionRequest(
            "USER_READ", "USER", "READ", "Updated description"
        );

        Permission updatedPermission = Permission.builder()
            .id(testPermissionId)
            .name("USER_READ")
            .resource("USER")
            .action("READ")
            .description("Updated description")
            .build();

        given(permissionRepository.findById(testPermissionId)).willReturn(Optional.of(testPermission));
        given(permissionRepository.save(testPermission)).willReturn(updatedPermission);
        given(permissionMapper.mapToResponse(updatedPermission)).willReturn(
            new PermissionResponse(testPermissionId, "USER_READ", "USER", "READ",
                "Updated description", null, null, null, "admin")
        );

        // When
        PermissionResponse result = permissionService.updatePermission(testPermissionId, sameNameRequest, "admin");

        // Then
        assertThat(result).isNotNull();
        verify(permissionRepository).save(testPermission);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent permission")
    void shouldThrowExceptionWhenUpdatingNonExistentPermission() {
        // Given
        given(permissionRepository.findById(testPermissionId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> permissionService.updatePermission(testPermissionId, updatePermissionRequest, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Permission not found with id: " + testPermissionId);
    }

    @Test
    @DisplayName("Should delete permission successfully")
    void shouldDeletePermissionSuccessfully() {
        // Given
        given(permissionRepository.findById(testPermissionId)).willReturn(Optional.of(testPermission));

        // When
        permissionService.deletePermission(testPermissionId, "admin");

        // Then
        verify(permissionRepository).delete(testPermission);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent permission")
    void shouldThrowExceptionWhenDeletingNonExistentPermission() {
        // Given
        given(permissionRepository.findById(testPermissionId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> permissionService.deletePermission(testPermissionId, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Permission not found with id: " + testPermissionId);

        verify(permissionRepository, never()).delete(any());
    }
}
