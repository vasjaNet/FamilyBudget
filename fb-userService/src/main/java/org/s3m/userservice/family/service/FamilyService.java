package org.s3m.userservice.family.service;

import lombok.AllArgsConstructor;
import org.s3m.userservice.family.dto.*;
import org.s3m.userservice.family.entity.Family;
import org.s3m.userservice.family.entity.FamilyMember;
import org.s3m.userservice.family.mapper.FamilyMapper;
import org.s3m.userservice.family.mapper.FamilyMemberMapper;
import org.s3m.userservice.family.repository.FamilyMemberRepository;
import org.s3m.userservice.family.repository.FamilyRepository;
import org.s3m.userservice.user.entity.User;
import org.s3m.userservice.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class FamilyService {
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;
    private final FamilyMapper familyMapper;
    private final FamilyMemberMapper familyMemberMapper;

    public FamilyResponse createFamily(CreateFamilyRequest request, UUID ownerId, String createdBy) {
        Family family = familyMapper.mapToEntity(request);
        family.setOwnerId(ownerId);
        family.setCreatedBy(createdBy);

        Family savedFamily = familyRepository.save(family);

        FamilyMember ownerMember = FamilyMember.builder()
                .familyId(savedFamily.getId())
                .userId(ownerId)
                .role(FamilyMember.FamilyRole.OWNER)
                .createdBy(createdBy)
                .build();
        familyMemberRepository.save(ownerMember);

        return buildFamilyResponse(savedFamily);
    }

    @Transactional(readOnly = true)
    public FamilyResponse getFamilyById(UUID familyId) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found"));
        return buildFamilyResponse(family);
    }

    @Transactional(readOnly = true)
    public List<FamilyResponse> getFamiliesByOwnerId(UUID ownerId) {
        return familyRepository.findByOwnerId(ownerId).stream()
                .map(this::buildFamilyResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FamilyResponse> getUserFamilies(UUID userId) {
        return familyRepository.findByUserId(userId).stream()
                .map(this::buildFamilyResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FamilyResponse> getAllFamilies() {
        return familyRepository.findAll().stream()
                .map(this::buildFamilyResponse)
                .toList();
    }

    public FamilyResponse updateFamily(UUID familyId, UpdateFamilyRequest request, String updatedBy) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found"));
        familyMapper.mapToEntity(request, family);
        family.setUpdatedBy(updatedBy);

        Family updatedFamily = familyRepository.save(family);
        return buildFamilyResponse(updatedFamily);
    }

    public void deleteFamily(UUID familyId, String deletedBy) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found"));

        // Delete all members first
        familyMemberRepository.deleteByFamilyId(familyId);

        familyRepository.delete(family);
    }

    public FamilyMemberResponse addMember(UUID familyId, AddMemberRequest request, String addedBy) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found"));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (familyMemberRepository.existsByFamilyIdAndUserId(familyId, request.userId())) {
            throw new IllegalArgumentException("User is already a member of this family");
        }

        FamilyMember member = FamilyMember.builder()
                .familyId(familyId)
                .userId(request.userId())
                .role(request.role())
                .createdBy(addedBy)
                .build();

        FamilyMember savedMember = familyMemberRepository.save(member);
        return familyMemberMapper.mapToResponse(savedMember, user.getUsername(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public List<FamilyMemberResponse> getMembersByFamilyId(UUID familyId) {
        if (!familyRepository.existsById(familyId)) {
            throw new IllegalArgumentException("Family not found");
        }

        List<FamilyMember> members = familyMemberRepository.findByFamilyId(familyId);
        return members.stream()
                .map(member -> {
                    User user = userRepository.findById(member.getUserId())
                            .orElse(null);
                    String username = user != null ? user.getUsername() : "Unknown";
                    String email = user != null ? user.getEmail() : "Unknown";
                    return familyMemberMapper.mapToResponse(member, username, email);
                })
                .toList();
    }

    public void removeMember(UUID familyId, UUID userId) {
        if (!familyRepository.existsById(familyId)) {
            throw new IllegalArgumentException("Family not found");
        }

        FamilyMember member = familyMemberRepository.findByFamilyIdAndUserId(familyId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (member.getRole() == FamilyMember.FamilyRole.OWNER) {
            throw new IllegalArgumentException("Cannot remove the owner from the family");
        }

        familyMemberRepository.delete(member);
    }

    public FamilyMemberResponse updateMemberRole(UUID familyId, UUID userId, FamilyMember.FamilyRole newRole, String updatedBy) {
        if (!familyRepository.existsById(familyId)) {
            throw new IllegalArgumentException("Family not found");
        }

        FamilyMember member = familyMemberRepository.findByFamilyIdAndUserId(familyId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (member.getRole() == FamilyMember.FamilyRole.OWNER) {
            throw new IllegalArgumentException("Cannot change the owner's role");
        }

        member.setRole(newRole);
        FamilyMember updatedMember = familyMemberRepository.save(member);

        User user = userRepository.findById(userId)
                .orElse(null);
        String username = user != null ? user.getUsername() : "Unknown";
        String email = user != null ? user.getEmail() : "Unknown";

        return familyMemberMapper.mapToResponse(updatedMember, username, email);
    }

    private FamilyResponse buildFamilyResponse(Family family) {
        // Get owner username
        User owner = userRepository.findById(family.getOwnerId()).orElse(null);
        String ownerUsername = owner != null ? owner.getUsername() : "Unknown";

        // Get member count
        int memberCount = familyMemberRepository.findByFamilyId(family.getId()).size();

        return familyMapper.mapToResponse(family, ownerUsername, memberCount);
    }
}