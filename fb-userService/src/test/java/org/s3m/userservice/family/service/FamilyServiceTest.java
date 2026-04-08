package org.s3m.userservice.family.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.s3m.userservice.family.dto.*;
import org.s3m.userservice.family.entity.Family;
import org.s3m.userservice.family.entity.FamilyMember;
import org.s3m.userservice.family.mapper.FamilyMapper;
import org.s3m.userservice.family.mapper.FamilyMemberMapper;
import org.s3m.userservice.family.repository.FamilyMemberRepository;
import org.s3m.userservice.family.repository.FamilyRepository;
import org.s3m.userservice.user.entity.User;
import org.s3m.userservice.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link FamilyService}
 * Tests business logic in isolation using Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Family Service Tests")
class FamilyServiceTest {

    @Mock
    private FamilyRepository familyRepository;

    @Mock
    private FamilyMemberRepository familyMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FamilyMapper familyMapper;

    @Mock
    private FamilyMemberMapper familyMemberMapper;

    @InjectMocks
    private FamilyService familyService;

    private UUID testFamilyId;
    private UUID testUserId;
    private UUID ownerId;
    private Family testFamily;
    private User testUser;
    private User ownerUser;
    private FamilyResponse testFamilyResponse;

    @BeforeEach
    void setUp() {
        testFamilyId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        testFamily = Family.builder()
            .id(testFamilyId)
            .name("Test Family")
            .description("Test Description")
            .ownerId(ownerId)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .createdBy("system")
            .build();

        testUser = User.builder()
            .id(testUserId)
            .username("testuser")
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .build();

        ownerUser = User.builder()
            .id(ownerId)
            .username("owner")
            .email("owner@example.com")
            .firstName("Owner")
            .lastName("User")
            .build();

        testFamilyResponse = new FamilyResponse(
            testFamilyId,
            "Test Family",
            "Test Description",
            ownerId,
            "owner",
            testFamily.getCreatedAt(),
            testFamily.getUpdatedAt(),
            "system",
            null,
            1
        );
    }

    @Test
    @DisplayName("Should create family successfully with owner as first member")
    void shouldCreateFamilySuccessfully() {
        // Given
        CreateFamilyRequest request = new CreateFamilyRequest("New Family", "Description");

        Family familyToSave = Family.builder()
            .name("New Family")
            .description("Description")
            .build();

        Family savedFamily = Family.builder()
            .id(UUID.randomUUID())
            .name("New Family")
            .description("Description")
            .ownerId(ownerId)
            .createdBy("admin")
            .build();

        FamilyMember ownerMember = FamilyMember.builder()
            .family(savedFamily)
            .userId(ownerId)
            .role(FamilyMember.FamilyRole.OWNER)
            .createdBy("admin")
            .build();

        given(familyMapper.mapToEntity(request)).willReturn(familyToSave);
        given(familyRepository.save(familyToSave)).willReturn(savedFamily);
        given(familyMemberRepository.save(any(FamilyMember.class))).willReturn(ownerMember);
        given(userRepository.findById(ownerId)).willReturn(Optional.of(ownerUser));
        given(familyMemberRepository.findByFamilyId(savedFamily.getId())).willReturn(List.of(ownerMember));
        given(familyMapper.mapToResponse(eq(savedFamily), eq("owner"), eq(1))).willReturn(
            new FamilyResponse(savedFamily.getId(), "New Family", "Description", ownerId, "owner",
                savedFamily.getCreatedAt(), savedFamily.getUpdatedAt(), "admin", null, 1)
        );

        // When
        FamilyResponse result = familyService.createFamily(request, ownerId, "admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("New Family");
        verify(familyRepository).save(familyToSave);
        verify(familyMemberRepository).save(any(FamilyMember.class));
    }

    @Test
    @DisplayName("Should get family by ID successfully")
    void shouldGetFamilyByIdSuccessfully() {
        // Given
        FamilyMember member = FamilyMember.builder()
            .id(UUID.randomUUID())
            .family(testFamily)
            .userId(ownerId)
            .role(FamilyMember.FamilyRole.OWNER)
            .build();

        given(familyRepository.findById(testFamilyId)).willReturn(Optional.of(testFamily));
        given(userRepository.findById(ownerId)).willReturn(Optional.of(ownerUser));
        given(familyMemberRepository.findByFamilyId(testFamilyId)).willReturn(List.of(member));
        given(familyMapper.mapToResponse(testFamily, "owner", 1)).willReturn(testFamilyResponse);

        // When
        FamilyResponse result = familyService.getFamilyById(testFamilyId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testFamilyId);
        assertThat(result.name()).isEqualTo("Test Family");
    }

    @Test
    @DisplayName("Should throw exception when family not found by ID")
    void shouldThrowExceptionWhenFamilyNotFoundById() {
        // Given
        given(familyRepository.findById(testFamilyId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> familyService.getFamilyById(testFamilyId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family not found");
    }

    @Test
    @DisplayName("Should get families by owner ID")
    void shouldGetFamiliesByOwnerId() {
        // Given
        given(familyRepository.findByOwnerId(ownerId)).willReturn(List.of(testFamily));
        given(userRepository.findById(ownerId)).willReturn(Optional.of(ownerUser));
        given(familyMemberRepository.findByFamilyId(testFamilyId)).willReturn(List.of());
        given(familyMapper.mapToResponse(testFamily, "owner", 0)).willReturn(testFamilyResponse);

        // When
        List<FamilyResponse> result = familyService.getFamiliesByOwnerId(ownerId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).ownerId()).isEqualTo(ownerId);
    }

    @Test
    @DisplayName("Should get families for user")
    void shouldGetUserFamilies() {
        // Given
        FamilyMember membership = FamilyMember.builder()
            .id(UUID.randomUUID())
            .family(testFamily)
            .userId(testUserId)
            .role(FamilyMember.FamilyRole.FULL_ACCESS)
            .build();

        given(familyRepository.findByUserId(testUserId)).willReturn(List.of(testFamily));
        given(userRepository.findById(ownerId)).willReturn(Optional.of(ownerUser));
        given(familyMemberRepository.findByFamilyId(testFamilyId)).willReturn(List.of(membership));
        given(familyMapper.mapToResponse(testFamily, "owner", 1)).willReturn(testFamilyResponse);

        // When
        List<FamilyResponse> result = familyService.getUserFamilies(testUserId);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should get all families")
    void shouldGetAllFamilies() {
        // Given
        given(familyRepository.findAll()).willReturn(List.of(testFamily));
        given(userRepository.findById(ownerId)).willReturn(Optional.of(ownerUser));
        given(familyMemberRepository.findByFamilyId(testFamilyId)).willReturn(List.of());
        given(familyMapper.mapToResponse(testFamily, "owner", 0)).willReturn(testFamilyResponse);

        // When
        List<FamilyResponse> result = familyService.getAllFamilies();

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should return empty list when no families exist")
    void shouldReturnEmptyListWhenNoFamiliesExist() {
        // Given
        given(familyRepository.findAll()).willReturn(List.of());

        // When
        List<FamilyResponse> result = familyService.getAllFamilies();

        // Then
        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("Should throw exception when updating non-existent family")
    void shouldThrowExceptionWhenUpdatingNonExistentFamily() {
        // Given
        UpdateFamilyRequest request = new UpdateFamilyRequest("Updated Name", "Updated Description");
        given(familyRepository.findById(testFamilyId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> familyService.updateFamily(testFamilyId, request, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family not found");
    }

    @Test
    @DisplayName("Should delete family and all its members")
    void shouldDeleteFamilyAndMembers() {
        // Given
        given(familyRepository.findById(testFamilyId)).willReturn(Optional.of(testFamily));

        // When
        familyService.deleteFamily(testFamilyId, "admin");

        // Then
        verify(familyMemberRepository).deleteByFamilyId(testFamilyId);
        verify(familyRepository).delete(testFamily);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent family")
    void shouldThrowExceptionWhenDeletingNonExistentFamily() {
        // Given
        given(familyRepository.findById(testFamilyId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> familyService.deleteFamily(testFamilyId, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family not found");

        verify(familyMemberRepository, never()).deleteByFamilyId(any());
    }

    @Test
    @DisplayName("Should add member to family successfully")
    void shouldAddMemberToFamilySuccessfully() {
        // Given
        AddMemberRequest request = new AddMemberRequest(testUserId, FamilyMember.FamilyRole.FULL_ACCESS);

        FamilyMember savedMember = FamilyMember.builder()
            .id(UUID.randomUUID())
            .family(testFamily)
            .userId(testUserId)
            .role(FamilyMember.FamilyRole.FULL_ACCESS)
            .createdBy("admin")
            .build();

        FamilyMemberResponse expectedResponse = new FamilyMemberResponse(
            savedMember.getId(),
            testFamilyId,
            testUserId,
            "testuser",
            "test@example.com",
            FamilyMember.FamilyRole.FULL_ACCESS,
            savedMember.getCreatedAt(),
            "admin"
        );

        given(familyRepository.findById(testFamilyId)).willReturn(Optional.of(testFamily));
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        given(familyMemberRepository.existsByFamilyIdAndUserId(testFamilyId, testUserId)).willReturn(false);
        given(familyMemberRepository.save(any(FamilyMember.class))).willReturn(savedMember);
        given(familyMemberMapper.mapToResponse(savedMember, "testuser", "test@example.com"))
            .willReturn(expectedResponse);

        // When
        FamilyMemberResponse result = familyService.addMember(testFamilyId, request, "admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        verify(familyMemberRepository).save(any(FamilyMember.class));
    }

    @Test
    @DisplayName("Should throw exception when adding member to non-existent family")
    void shouldThrowExceptionWhenAddingMemberToNonExistentFamily() {
        // Given
        AddMemberRequest request = new AddMemberRequest(testUserId, FamilyMember.FamilyRole.FULL_ACCESS);
        given(familyRepository.findById(testFamilyId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> familyService.addMember(testFamilyId, request, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family not found");
    }

    @Test
    @DisplayName("Should throw exception when adding non-existent user as member")
    void shouldThrowExceptionWhenAddingNonExistentUserAsMember() {
        // Given
        AddMemberRequest request = new AddMemberRequest(testUserId, FamilyMember.FamilyRole.FULL_ACCESS);
        given(familyRepository.findById(testFamilyId)).willReturn(Optional.of(testFamily));
        given(userRepository.findById(testUserId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> familyService.addMember(testFamilyId, request, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");
    }

    @Test
    @DisplayName("Should throw exception when adding existing member")
    void shouldThrowExceptionWhenAddingExistingMember() {
        // Given
        AddMemberRequest request = new AddMemberRequest(testUserId, FamilyMember.FamilyRole.FULL_ACCESS);
        given(familyRepository.findById(testFamilyId)).willReturn(Optional.of(testFamily));
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        given(familyMemberRepository.existsByFamilyIdAndUserId(testFamilyId, testUserId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> familyService.addMember(testFamilyId, request, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User is already a member of this family");

        verify(familyMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get members by family ID")
    void shouldGetMembersByFamilyId() {
        // Given
        FamilyMember member = FamilyMember.builder()
            .id(UUID.randomUUID())
            .family(testFamily)
            .userId(testUserId)
            .role(FamilyMember.FamilyRole.FULL_ACCESS)
            .build();

        FamilyMemberResponse memberResponse = new FamilyMemberResponse(
            member.getId(),
            testFamilyId,
            testUserId,
            "testuser",
            "test@example.com",
            FamilyMember.FamilyRole.FULL_ACCESS,
            member.getCreatedAt(),
            null
        );

        given(familyRepository.existsById(testFamilyId)).willReturn(true);
        given(familyMemberRepository.findByFamilyId(testFamilyId)).willReturn(List.of(member));
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        given(familyMemberMapper.mapToResponse(member, "testuser", "test@example.com")).willReturn(memberResponse);

        // When
        List<FamilyMemberResponse> result = familyService.getMembersByFamilyId(testFamilyId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should throw exception when getting members of non-existent family")
    void shouldThrowExceptionWhenGettingMembersOfNonExistentFamily() {
        // Given
        given(familyRepository.existsById(testFamilyId)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> familyService.getMembersByFamilyId(testFamilyId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family not found");
    }

    @Test
    @DisplayName("Should remove member from family successfully")
    void shouldRemoveMemberSuccessfully() {
        // Given
        FamilyMember member = FamilyMember.builder()
            .id(UUID.randomUUID())
            .family(testFamily)
            .userId(testUserId)
            .role(FamilyMember.FamilyRole.FULL_ACCESS)
            .build();

        given(familyRepository.existsById(testFamilyId)).willReturn(true);
        given(familyMemberRepository.findByFamilyIdAndUserId(testFamilyId, testUserId)).willReturn(Optional.of(member));

        // When
        familyService.removeMember(testFamilyId, testUserId);

        // Then
        verify(familyMemberRepository).delete(member);
    }

    @Test
    @DisplayName("Should throw exception when removing member from non-existent family")
    void shouldThrowExceptionWhenRemovingMemberFromNonExistentFamily() {
        // Given
        given(familyRepository.existsById(testFamilyId)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> familyService.removeMember(testFamilyId, testUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family not found");
    }

    @Test
    @DisplayName("Should throw exception when removing non-existent member")
    void shouldThrowExceptionWhenRemovingNonExistentMember() {
        // Given
        given(familyRepository.existsById(testFamilyId)).willReturn(true);
        given(familyMemberRepository.findByFamilyIdAndUserId(testFamilyId, testUserId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> familyService.removeMember(testFamilyId, testUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Member not found");
    }

    @Test
    @DisplayName("Should throw exception when removing owner from family")
    void shouldThrowExceptionWhenRemovingOwner() {
        // Given
        FamilyMember ownerMember = FamilyMember.builder()
            .id(UUID.randomUUID())
            .family(testFamily)
            .userId(ownerId)
            .role(FamilyMember.FamilyRole.OWNER)
            .build();

        given(familyRepository.existsById(testFamilyId)).willReturn(true);
        given(familyMemberRepository.findByFamilyIdAndUserId(testFamilyId, ownerId)).willReturn(Optional.of(ownerMember));

        // When & Then
        assertThatThrownBy(() -> familyService.removeMember(testFamilyId, ownerId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot remove the owner from the family");

        verify(familyMemberRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should update member role successfully")
    void shouldUpdateMemberRoleSuccessfully() {
        // Given
        FamilyMember member = FamilyMember.builder()
            .id(UUID.randomUUID())
            .family(testFamily)
            .userId(testUserId)
            .role(FamilyMember.FamilyRole.INFO_ONLY)
            .build();

        FamilyMember updatedMember = FamilyMember.builder()
            .id(member.getId())
            .family(testFamily)
            .userId(testUserId)
            .role(FamilyMember.FamilyRole.FULL_ACCESS)
            .build();

        FamilyMemberResponse expectedResponse = new FamilyMemberResponse(
            member.getId(),
            testFamilyId,
            testUserId,
            "testuser",
            "test@example.com",
            FamilyMember.FamilyRole.FULL_ACCESS,
            member.getCreatedAt(),
            null
        );

        given(familyRepository.existsById(testFamilyId)).willReturn(true);
        given(familyMemberRepository.findByFamilyIdAndUserId(testFamilyId, testUserId)).willReturn(Optional.of(member));
        given(familyMemberRepository.save(member)).willReturn(updatedMember);
        given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
        given(familyMemberMapper.mapToResponse(updatedMember, "testuser", "test@example.com")).willReturn(expectedResponse);

        // When
        FamilyMemberResponse result = familyService.updateMemberRole(
            testFamilyId, testUserId, FamilyMember.FamilyRole.FULL_ACCESS, "admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(FamilyMember.FamilyRole.FULL_ACCESS);
    }

    @Test
    @DisplayName("Should throw exception when updating role in non-existent family")
    void shouldThrowExceptionWhenUpdatingRoleInNonExistentFamily() {
        // Given
        given(familyRepository.existsById(testFamilyId)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> familyService.updateMemberRole(
            testFamilyId, testUserId, FamilyMember.FamilyRole.FULL_ACCESS, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family not found");
    }

    @Test
    @DisplayName("Should throw exception when updating role of non-existent member")
    void shouldThrowExceptionWhenUpdatingRoleOfNonExistentMember() {
        // Given
        given(familyRepository.existsById(testFamilyId)).willReturn(true);
        given(familyMemberRepository.findByFamilyIdAndUserId(testFamilyId, testUserId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> familyService.updateMemberRole(
            testFamilyId, testUserId, FamilyMember.FamilyRole.FULL_ACCESS, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Member not found");
    }

    @Test
    @DisplayName("Should throw exception when changing owner's role")
    void shouldThrowExceptionWhenChangingOwnerRole() {
        // Given
        FamilyMember ownerMember = FamilyMember.builder()
            .id(UUID.randomUUID())
            .family(testFamily)
            .userId(ownerId)
            .role(FamilyMember.FamilyRole.OWNER)
            .build();

        given(familyRepository.existsById(testFamilyId)).willReturn(true);
        given(familyMemberRepository.findByFamilyIdAndUserId(testFamilyId, ownerId)).willReturn(Optional.of(ownerMember));

        // When & Then
        assertThatThrownBy(() -> familyService.updateMemberRole(
            testFamilyId, ownerId, FamilyMember.FamilyRole.FULL_ACCESS, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot change the owner's role");

        verify(familyMemberRepository, never()).save(any());
    }
}
