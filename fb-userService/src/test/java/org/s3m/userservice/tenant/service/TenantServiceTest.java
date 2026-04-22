package org.s3m.userservice.tenant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.s3m.userservice.tenant.dto.CreateTenantRequest;
import org.s3m.userservice.tenant.dto.TenantResponse;
import org.s3m.userservice.tenant.dto.UpdateTenantRequest;
import org.s3m.userservice.tenant.entity.Tenant;
import org.s3m.userservice.tenant.entity.TenantType;
import org.s3m.userservice.tenant.mapper.TenantMapper;
import org.s3m.userservice.tenant.repository.TenantRepository;

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
 * Unit tests for {@link TenantService}
 * Tests business logic in isolation using Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tenant Service Tests")
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private TenantMapper tenantMapper;

    @InjectMocks
    private TenantService tenantService;

    private UUID testTenantId;
    private Tenant testTenant;
    private TenantResponse testTenantResponse;
    private CreateTenantRequest createTenantRequest;
    private UpdateTenantRequest updateTenantRequest;

    @BeforeEach
    void setUp() {
        testTenantId = UUID.randomUUID();

        testTenant = Tenant.builder()
            .id(testTenantId)
            .name("Personal Tenant")
            .description("My personal tenant")
            .type(TenantType.PERSONAL)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .createdBy("system")
            .updatedBy("system")
            .build();

        testTenantResponse = new TenantResponse(
            testTenantId,
            "Personal Tenant",
            "My personal tenant",
            TenantType.PERSONAL,
            testTenant.getCreatedAt(),
            testTenant.getUpdatedAt(),
            "system",
            "system",
                List.of()
        );

        createTenantRequest = new CreateTenantRequest(
            "Business Tenant",
            "My business tenant",
            TenantType.BUSINESS
        );

        updateTenantRequest = new UpdateTenantRequest(
            "Updated Tenant",
            "Updated description",
            TenantType.FAMILY
        );
    }

    @Test
    @DisplayName("Should create tenant successfully")
    void shouldCreateTenantSuccessfully() {
        // Given
        Tenant tenantToSave = Tenant.builder()
            .name("Business Tenant")
            .description("My business tenant")
            .type(TenantType.BUSINESS)
            .build();

        Tenant savedTenant = Tenant.builder()
            .id(UUID.randomUUID())
            .name("Business Tenant")
            .description("My business tenant")
            .type(TenantType.BUSINESS)
            .createdBy("admin")
            .build();

        given(tenantRepository.existsByName("Business Tenant")).willReturn(false);
        given(tenantMapper.mapToEntity(createTenantRequest)).willReturn(tenantToSave);
        given(tenantRepository.save(tenantToSave)).willReturn(savedTenant);
        given(tenantMapper.mapToResponse(savedTenant)).willReturn(
            new TenantResponse(savedTenant.getId(), "Business Tenant", "My business tenant",
                TenantType.BUSINESS, null, null, "admin", null, List.of())
        );

        // When
        TenantResponse result = tenantService.createTenant(createTenantRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Business Tenant");
        assertThat(result.type()).isEqualTo(TenantType.BUSINESS);
        verify(tenantRepository).save(tenantToSave);
    }

    @Test
    @DisplayName("Should throw exception when creating tenant with existing name")
    void shouldThrowExceptionWhenCreatingTenantWithExistingName() {
        // Given
        given(tenantRepository.existsByName("Business Tenant")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> tenantService.createTenant(createTenantRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tenant with name 'Business Tenant' already exists");

        verify(tenantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get tenant by ID successfully")
    void shouldGetTenantByIdSuccessfully() {
        // Given
        given(tenantRepository.findById(testTenantId)).willReturn(Optional.of(testTenant));
        given(tenantMapper.mapToResponse(testTenant)).willReturn(testTenantResponse);

        // When
        TenantResponse result = tenantService.getTenantById(testTenantId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testTenantId);
        assertThat(result.name()).isEqualTo("Personal Tenant");
    }

    @Test
    @DisplayName("Should throw exception when tenant not found by ID")
    void shouldThrowExceptionWhenTenantNotFoundById() {
        // Given
        given(tenantRepository.findById(testTenantId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tenantService.getTenantById(testTenantId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tenant not found");
    }

    @Test
    @DisplayName("Should get tenant by name successfully")
    void shouldGetTenantByNameSuccessfully() {
        // Given
        given(tenantRepository.findByName("Personal Tenant")).willReturn(Optional.of(testTenant));
        given(tenantMapper.mapToResponse(testTenant)).willReturn(testTenantResponse);

        // When
        TenantResponse result = tenantService.getTenantByName("Personal Tenant");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Personal Tenant");
    }

    @Test
    @DisplayName("Should throw exception when tenant not found by name")
    void shouldThrowExceptionWhenTenantNotFoundByName() {
        // Given
        given(tenantRepository.findByName("Unknown")).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tenantService.getTenantByName("Unknown"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tenant not found");
    }

    @Test
    @DisplayName("Should get all tenants")
    void shouldGetAllTenants() {
        // Given
        Tenant tenant2 = Tenant.builder()
            .id(UUID.randomUUID())
            .name("Business Tenant")
            .description("Business tenant")
            .type(TenantType.BUSINESS)
            .build();

        given(tenantRepository.findAll()).willReturn(List.of(testTenant, tenant2));
        given(tenantMapper.mapToResponse(testTenant)).willReturn(testTenantResponse);
        given(tenantMapper.mapToResponse(tenant2)).willReturn(
            new TenantResponse(tenant2.getId(), "Business Tenant", "Business tenant",
                TenantType.BUSINESS, null, null, null, null, List.of())
        );

        // When
        List<TenantResponse> result = tenantService.getAllTenants();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Personal Tenant");
        assertThat(result.get(1).name()).isEqualTo("Business Tenant");
    }

    @Test
    @DisplayName("Should return empty list when no tenants exist")
    void shouldReturnEmptyListWhenNoTenantsExist() {
        // Given
        given(tenantRepository.findAll()).willReturn(List.of());

        // When
        List<TenantResponse> result = tenantService.getAllTenants();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should update tenant successfully")
    void shouldUpdateTenantSuccessfully() {
        // Given
        Tenant updatedTenant = Tenant.builder()
            .id(testTenantId)
            .name("Updated Tenant")
            .description("Updated description")
            .type(TenantType.FAMILY)
            .createdBy("system")
            .updatedBy("admin")
            .build();

        given(tenantRepository.findById(testTenantId)).willReturn(Optional.of(testTenant));
        given(tenantRepository.existsByName("Updated Tenant")).willReturn(false);
        given(tenantRepository.save(testTenant)).willReturn(updatedTenant);
        given(tenantMapper.mapToResponse(updatedTenant)).willReturn(
            new TenantResponse(testTenantId, "Updated Tenant", "Updated description",
                TenantType.FAMILY, null, null, "system", "admin", List.of()));

        // When
        TenantResponse result = tenantService.updateTenant(testTenantId, updateTenantRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Updated Tenant");
        assertThat(result.type()).isEqualTo(TenantType.FAMILY);
    }

    @Test
    @DisplayName("Should throw exception when updating tenant with existing name")
    void shouldThrowExceptionWhenUpdatingWithExistingName() {
        // Given
        given(tenantRepository.findById(testTenantId)).willReturn(Optional.of(testTenant));
        given(tenantRepository.existsByName("Updated Tenant")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> tenantService.updateTenant(testTenantId, updateTenantRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tenant with name 'Updated Tenant' already exists");
    }

    @Test
    @DisplayName("Should allow updating tenant when name is unchanged")
    void shouldAllowUpdateWhenNameUnchanged() {
        // Given
        UpdateTenantRequest sameNameRequest = new UpdateTenantRequest("Personal Tenant", "Updated description", TenantType.FAMILY);

        Tenant updatedTenant = Tenant.builder()
            .id(testTenantId)
            .name("Personal Tenant")
            .description("Updated description")
            .type(TenantType.FAMILY)
            .build();

        given(tenantRepository.findById(testTenantId)).willReturn(Optional.of(testTenant));
        given(tenantRepository.save(testTenant)).willReturn(updatedTenant);
        given(tenantMapper.mapToResponse(updatedTenant)).willReturn(
            new TenantResponse(testTenantId, "Personal Tenant", "Updated description",
                TenantType.FAMILY, null, null, null, "admin", List.of())
        );

        // When
        TenantResponse result = tenantService.updateTenant(testTenantId, sameNameRequest);

        // Then
        assertThat(result).isNotNull();
        verify(tenantRepository).save(testTenant);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent tenant")
    void shouldThrowExceptionWhenUpdatingNonExistentTenant() {
        // Given
        given(tenantRepository.findById(testTenantId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tenantService.updateTenant(testTenantId, updateTenantRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tenant not found");
    }

    @Test
    @DisplayName("Should delete tenant successfully")
    void shouldDeleteTenantSuccessfully() {
        // Given
        given(tenantRepository.findById(testTenantId)).willReturn(Optional.of(testTenant));

        // When
        tenantService.deleteTenant(testTenantId);

        // Then
        verify(tenantRepository).delete(testTenant);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent tenant")
    void shouldThrowExceptionWhenDeletingNonExistentTenant() {
        // Given
        given(tenantRepository.findById(testTenantId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tenantService.deleteTenant(testTenantId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tenant not found");

        verify(tenantRepository, never()).delete(any());
    }
}
