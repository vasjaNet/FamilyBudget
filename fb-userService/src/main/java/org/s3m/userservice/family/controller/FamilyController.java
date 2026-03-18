package org.s3m.userservice.family.controller;

import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ApiResponse;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.family.dto.*;
import org.s3m.userservice.family.entity.FamilyMember;
import org.s3m.userservice.family.service.FamilyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_PREFIX + "/families")
@AllArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping
    public ResponseEntity<ApiResponse<FamilyResponse>> createFamily(
            @RequestBody CreateFamilyRequest request,
            @RequestHeader(value = "X-Owner-Id") UUID ownerId,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        FamilyResponse response = familyService.createFamily(request, ownerId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Family created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FamilyResponse>> getFamilyById(@PathVariable UUID id) {
        FamilyResponse response = familyService.getFamilyById(id);
        return ResponseEntity.ok(ApiResponse.success("Family retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FamilyResponse>>> getAllFamilies(
            @RequestHeader(value = "X-Owner-Id", required = false) UUID ownerId) {
        List<FamilyResponse> response;
        if (ownerId != null) {
            response = familyService.getFamiliesByOwnerId(ownerId);
        } else {
            response = familyService.getAllFamilies();
        }
        return ResponseEntity.ok(ApiResponse.success("Families retrieved successfully", response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FamilyResponse>>> getUserFamilies(@PathVariable UUID userId) {
        List<FamilyResponse> response = familyService.getUserFamilies(userId);
        return ResponseEntity.ok(ApiResponse.success("User families retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FamilyResponse>> updateFamily(
            @PathVariable UUID id,
            @RequestBody UpdateFamilyRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        FamilyResponse response = familyService.updateFamily(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Family updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFamily(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        familyService.deleteFamily(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Family deleted successfully", null));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<FamilyMemberResponse>> addMember(
            @PathVariable UUID id,
            @RequestBody AddMemberRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        FamilyMemberResponse response = familyService.addMember(id, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member added successfully", response));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<FamilyMemberResponse>>> getMembers(@PathVariable UUID id) {
        List<FamilyMemberResponse> response = familyService.getMembersByFamilyId(id);
        return ResponseEntity.ok(ApiResponse.success("Members retrieved successfully", response));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        familyService.removeMember(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Member removed successfully", null));
    }

    @PutMapping("/{id}/members/{userId}/role")
    public ResponseEntity<ApiResponse<FamilyMemberResponse>> updateMemberRole(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @RequestParam FamilyMember.FamilyRole role,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userIdHeader) {
        FamilyMemberResponse response = familyService.updateMemberRole(id, userId, role, userIdHeader);
        return ResponseEntity.ok(ApiResponse.success("Member role updated successfully", response));
    }

}