package org.s3m.userservice.permission.controller;

import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ApiResponse;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.permission.dto.CreatePermissionRequest;
import org.s3m.userservice.permission.dto.PermissionResponse;
import org.s3m.userservice.permission.dto.UpdatePermissionRequest;
import org.s3m.userservice.permission.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_PREFIX + "/permissions")
@AllArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(
            @RequestBody CreatePermissionRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        PermissionResponse response = permissionService.createPermission(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Permission created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionById(@PathVariable UUID id) {
        PermissionResponse response = permissionService.getPermissionById(id);
        return ResponseEntity.ok(ApiResponse.success("Permission retrieved successfully", response));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionByName(@PathVariable String name) {
        PermissionResponse response = permissionService.getPermissionByName(name);
        return ResponseEntity.ok(ApiResponse.success("Permission retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> response = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.success("Permissions retrieved successfully", response));
    }

    @GetMapping("/resource/{resource}")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissionsByResource(@PathVariable String resource) {
        List<PermissionResponse> response = permissionService.getPermissionsByResource(resource);
        return ResponseEntity.ok(ApiResponse.success("Permissions retrieved successfully", response));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissionsByAction(@PathVariable String action) {
        List<PermissionResponse> response = permissionService.getPermissionsByAction(action);
        return ResponseEntity.ok(ApiResponse.success("Permissions retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> updatePermission(
            @PathVariable UUID id,
            @RequestBody UpdatePermissionRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        PermissionResponse response = permissionService.updatePermission(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Permission updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        permissionService.deletePermission(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Permission deleted successfully", null));
    }

}