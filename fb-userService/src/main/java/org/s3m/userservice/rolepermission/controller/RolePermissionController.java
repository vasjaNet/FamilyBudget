package org.s3m.userservice.rolepermission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Role-Permission Assignments", description = "Assign and remove permissions from roles")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(AppConstants.API_PREFIX + "/role-permissions")
@AllArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @Operation(summary = "Assign a permission to a role")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Permission assigned to role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role or permission not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Assignment already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<RolePermissionResponse>> assignPermissionToRole(
            @RequestBody CreateRolePermissionRequest request) {
        RolePermissionResponse response = rolePermissionService.assignPermissionToRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Permission assigned to role successfully", response));
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RolePermissionResponse>> getRolePermissionById(@PathVariable UUID id) {
        RolePermissionResponse response = rolePermissionService.getRolePermissionById(id);
        return ResponseEntity.ok(ApiResponse.success("Role-Permission relationship retrieved successfully", response));
    }*/

    @Operation(summary = "Get a role-permission assignment by role and permission")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Assignment retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Assignment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/role/{roleId}/permission/{permissionId}")
    public ResponseEntity<ApiResponse<RolePermissionResponse>> getRolePermissionByRoleAndPermission(
            @Parameter(description = "Role UUID") @PathVariable UUID roleId,
            @Parameter(description = "Permission UUID") @PathVariable UUID permissionId) {
        RolePermissionResponse response = rolePermissionService.getRolePermissionByRoleAndPermission(roleId, permissionId);
        return ResponseEntity.ok(ApiResponse.success("Role-Permission relationship retrieved successfully", response));
    }

    @Operation(summary = "Get all permission assignments for a role")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Assignments retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/role/{roleId}")
    public ResponseEntity<ApiResponse<List<RolePermissionResponse>>> getRolePermissionsByRoleId(
            @Parameter(description = "Role UUID") @PathVariable UUID roleId) {
        List<RolePermissionResponse> response = rolePermissionService.getRolePermissionsByRoleId(roleId);
        return ResponseEntity.ok(ApiResponse.success("Role-Permissions retrieved successfully", response));
    }

    @Operation(summary = "Get all role assignments for a permission")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Assignments retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/permission/{permissionId}")
    public ResponseEntity<ApiResponse<List<RolePermissionResponse>>> getRolePermissionsByPermissionId(
            @Parameter(description = "Permission UUID") @PathVariable UUID permissionId) {
        List<RolePermissionResponse> response = rolePermissionService.getRolePermissionsByPermissionId(permissionId);
        return ResponseEntity.ok(ApiResponse.success("Role-Permissions retrieved successfully", response));
    }

    @Operation(summary = "Get all role-permission assignments")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Assignments retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<RolePermissionResponse>>> getAllRolePermissions() {
        List<RolePermissionResponse> response = rolePermissionService.getAllRolePermissions();
        return ResponseEntity.ok(ApiResponse.success("Role-Permissions retrieved successfully", response));
    }

    /*@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removePermissionFromRole(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        rolePermissionService.removePermissionFromRole(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Permission removed from role successfully", null));
    }*/

    @Operation(summary = "Remove a permission from a role")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Permission removed from role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Assignment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/role/{roleId}/permission/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> removePermissionFromRoleByIds(
            @Parameter(description = "Role UUID") @PathVariable UUID roleId,
            @Parameter(description = "Permission UUID") @PathVariable UUID permissionId) {
        rolePermissionService.removePermissionFromRoleByIds(roleId, permissionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Permission removed from role successfully", null));
    }

}
