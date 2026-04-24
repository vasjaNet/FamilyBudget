package org.s3m.userservice.permission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Permission Management", description = "CRUD operations for permissions")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(AppConstants.API_PREFIX + "/permissions")
@AllArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Create a permission")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Permission created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Permission already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(
            @RequestBody CreatePermissionRequest request,
            @Parameter(description = "ID of the user performing the action", example = "SYSTEM")
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        PermissionResponse response = permissionService.createPermission(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Permission created successfully", response));
    }

    @Operation(summary = "Get a permission by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permission retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Permission not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionById(
            @Parameter(description = "Permission UUID") @PathVariable UUID id) {
        PermissionResponse response = permissionService.getPermissionById(id);
        return ResponseEntity.ok(ApiResponse.success("Permission retrieved successfully", response));
    }

    @Operation(summary = "Get a permission by name")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permission retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Permission not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionByName(
            @Parameter(description = "Permission name") @PathVariable String name) {
        PermissionResponse response = permissionService.getPermissionByName(name);
        return ResponseEntity.ok(ApiResponse.success("Permission retrieved successfully", response));
    }

    @Operation(summary = "Get all permissions")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permissions retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> response = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.success("Permissions retrieved successfully", response));
    }

    @Operation(summary = "Get permissions by resource")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permissions retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/resource/{resource}")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissionsByResource(
            @Parameter(description = "Resource name") @PathVariable String resource) {
        List<PermissionResponse> response = permissionService.getPermissionsByResource(resource);
        return ResponseEntity.ok(ApiResponse.success("Permissions retrieved successfully", response));
    }

    @Operation(summary = "Get permissions by action")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permissions retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/action/{action}")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissionsByAction(
            @Parameter(description = "Action name") @PathVariable String action) {
        List<PermissionResponse> response = permissionService.getPermissionsByAction(action);
        return ResponseEntity.ok(ApiResponse.success("Permissions retrieved successfully", response));
    }

    @Operation(summary = "Update a permission")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permission updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Permission not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> updatePermission(
            @Parameter(description = "Permission UUID") @PathVariable UUID id,
            @RequestBody UpdatePermissionRequest request,
            @Parameter(description = "ID of the user performing the action", example = "SYSTEM")
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        PermissionResponse response = permissionService.updatePermission(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Permission updated successfully", response));
    }

    @Operation(summary = "Delete a permission")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Permission deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Permission not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(
            @Parameter(description = "Permission UUID") @PathVariable UUID id,
            @Parameter(description = "ID of the user performing the action", example = "SYSTEM")
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        permissionService.deletePermission(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Permission deleted successfully", null));
    }

}
