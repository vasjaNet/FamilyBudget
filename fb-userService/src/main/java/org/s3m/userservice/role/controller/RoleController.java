package org.s3m.userservice.role.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ApiResponse;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.role.dto.CreateRoleRequest;
import org.s3m.userservice.role.dto.RoleResponse;
import org.s3m.userservice.role.dto.UpdateRoleRequest;
import org.s3m.userservice.role.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Role Management", description = "CRUD operations for roles")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(AppConstants.API_PREFIX + "/roles")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Create a role")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Role created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Role already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(
            @RequestBody CreateRoleRequest request,
            @Parameter(description = "ID of the user performing the action", example = "SYSTEM")
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        RoleResponse response = roleService.createRole(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role created successfully", response));
    }

    @Operation(summary = "Get a role by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(
            @Parameter(description = "Role UUID") @PathVariable UUID id) {
        RoleResponse response = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success("Role retrieved successfully", response));
    }

    @Operation(summary = "Get a role by name")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleByName(
            @Parameter(description = "Role name") @PathVariable String name) {
        RoleResponse response = roleService.getRoleByName(name);
        return ResponseEntity.ok(ApiResponse.success("Role retrieved successfully", response));
    }

    @Operation(summary = "Get all roles")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Roles retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> response = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("Roles retrieved successfully", response));
    }

    @Operation(summary = "Update a role")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @Parameter(description = "Role UUID") @PathVariable UUID id,
            @RequestBody UpdateRoleRequest request,
            @Parameter(description = "ID of the user performing the action", example = "SYSTEM")
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        RoleResponse response = roleService.updateRole(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", response));
    }

    @Operation(summary = "Delete a role")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Role deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @Parameter(description = "Role UUID") @PathVariable UUID id,
            @Parameter(description = "ID of the user performing the action", example = "SYSTEM")
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        roleService.deleteRole(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Role deleted successfully", null));
    }

}
