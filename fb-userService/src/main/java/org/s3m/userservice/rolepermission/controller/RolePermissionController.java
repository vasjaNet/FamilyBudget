package org.s3m.userservice.rolepermission.controller;

import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ApiResponse;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.rolepermission.dto.CreateRolePermissionRequest;
import org.s3m.userservice.rolepermission.dto.RolePermissionResponse;
import org.s3m.userservice.rolepermission.service.RolePermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_PREFIX + "/role-permissions")
@AllArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<RolePermissionResponse>> assignPermissionToRole(
            @RequestBody CreateRolePermissionRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        RolePermissionResponse response = rolePermissionService.assignPermissionToRole(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Permission assigned to role successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RolePermissionResponse>> getRolePermissionById(@PathVariable UUID id) {
        RolePermissionResponse response = rolePermissionService.getRolePermissionById(id);
        return ResponseEntity.ok(ApiResponse.success("Role-Permission relationship retrieved successfully", response));
    }

    @GetMapping("/role/{roleId}/permission/{permissionId}")
    public ResponseEntity<ApiResponse<RolePermissionResponse>> getRolePermissionByRoleAndPermission(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId) {
        RolePermissionResponse response = rolePermissionService.getRolePermissionByRoleAndPermission(roleId, permissionId);
        return ResponseEntity.ok(ApiResponse.success("Role-Permission relationship retrieved successfully", response));
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<ApiResponse<List<RolePermissionResponse>>> getRolePermissionsByRoleId(@PathVariable UUID roleId) {
        List<RolePermissionResponse> response = rolePermissionService.getRolePermissionsByRoleId(roleId);
        return ResponseEntity.ok(ApiResponse.success("Role-Permissions retrieved successfully", response));
    }

    @GetMapping("/permission/{permissionId}")
    public ResponseEntity<ApiResponse<List<RolePermissionResponse>>> getRolePermissionsByPermissionId(@PathVariable UUID permissionId) {
        List<RolePermissionResponse> response = rolePermissionService.getRolePermissionsByPermissionId(permissionId);
        return ResponseEntity.ok(ApiResponse.success("Role-Permissions retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RolePermissionResponse>>> getAllRolePermissions() {
        List<RolePermissionResponse> response = rolePermissionService.getAllRolePermissions();
        return ResponseEntity.ok(ApiResponse.success("Role-Permissions retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removePermissionFromRole(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        rolePermissionService.removePermissionFromRole(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Permission removed from role successfully", null));
    }

    @DeleteMapping("/role/{roleId}/permission/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> removePermissionFromRoleByIds(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String changedBy) {
        rolePermissionService.removePermissionFromRoleByIds(roleId, permissionId, changedBy);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Permission removed from role successfully", null));
    }

}