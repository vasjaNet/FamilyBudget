package org.s3m.userservice.permission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ResponseWrapper;
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
            @ApiResponse(responseCode = "201", description = "Permission created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Permission already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<PermissionResponse>> createPermission(
            @RequestBody CreatePermissionRequest request) {
        PermissionResponse response = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("Permission created successfully", response));
    }

    @Operation(summary = "Get a permission by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Permission not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<PermissionResponse>> getPermissionById(
            @Parameter(description = "Permission UUID") @PathVariable UUID id) {
        PermissionResponse response = permissionService.getPermissionById(id);
        return ResponseEntity.ok(ResponseWrapper.success("Permission retrieved successfully", response));
    }

    @Operation(summary = "Get a permission by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Permission not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<ResponseWrapper<PermissionResponse>> getPermissionByName(
            @Parameter(description = "Permission name") @PathVariable String name) {
        PermissionResponse response = permissionService.getPermissionByName(name);
        return ResponseEntity.ok(ResponseWrapper.success("Permission retrieved successfully", response));
    }

    @Operation(summary = "Get all permissions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permissions retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> response = permissionService.getAllPermissions();
        return ResponseEntity.ok(ResponseWrapper.success("Permissions retrieved successfully", response));
    }

    @Operation(summary = "Get permissions by resource")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permissions retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/resource/{resource}")
    public ResponseEntity<ResponseWrapper<List<PermissionResponse>>> getPermissionsByResource(
            @Parameter(description = "Resource name") @PathVariable String resource) {
        List<PermissionResponse> response = permissionService.getPermissionsByResource(resource);
        return ResponseEntity.ok(ResponseWrapper.success("Permissions retrieved successfully", response));
    }

    @Operation(summary = "Get permissions by action")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permissions retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/action/{action}")
    public ResponseEntity<ResponseWrapper<List<PermissionResponse>>> getPermissionsByAction(
            @Parameter(description = "Action name") @PathVariable String action) {
        List<PermissionResponse> response = permissionService.getPermissionsByAction(action);
        return ResponseEntity.ok(ResponseWrapper.success("Permissions retrieved successfully", response));
    }

    @Operation(summary = "Update a permission")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Permission not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<PermissionResponse>> updatePermission(
            @Parameter(description = "Permission UUID") @PathVariable UUID id,
            @RequestBody UpdatePermissionRequest request) {
        PermissionResponse response = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("Permission updated successfully", response));
    }

    @Operation(summary = "Delete a permission")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Permission deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Permission not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deletePermission(
            @Parameter(description = "Permission UUID") @PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseWrapper.success("Permission deleted successfully", null));
    }

}
