package org.s3m.userservice.role.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ResponseWrapper;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.role.dto.CreateRoleRequest;
import org.s3m.userservice.role.dto.RoleResponse;
import org.s3m.userservice.role.dto.RoleResponseBasic;
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
            @ApiResponse(responseCode = "201", description = "Role created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Role already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<RoleResponse>> createRole(
            @RequestBody CreateRoleRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("Role created successfully", response));
    }

    @Operation(summary = "Get a role by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<RoleResponse>> getRoleById(
            @Parameter(description = "Role UUID") @PathVariable UUID id) {
        RoleResponse response = roleService.getRoleById(id);
        return ResponseEntity.ok(ResponseWrapper.success("Role retrieved successfully", response));
    }

    @Operation(summary = "Get a role by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<ResponseWrapper<RoleResponse>> getRoleByName(
            @Parameter(description = "Role name") @PathVariable String name) {
        RoleResponse response = roleService.getRoleByName(name);
        return ResponseEntity.ok(ResponseWrapper.success("Role retrieved successfully", response));
    }

    @Operation(summary = "Get all roles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roles retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> response = roleService.getAllRoles();
        return ResponseEntity.ok(ResponseWrapper.success("Roles retrieved successfully", response));
    }

    @GetMapping("/basic")
    public ResponseEntity<ResponseWrapper<List<RoleResponseBasic>>> getAllRolesBasic() {
        List<RoleResponseBasic> response = roleService.getAllRolesBasic();
        return ResponseEntity.ok(ResponseWrapper.success("Roles retrieved successfully", response));
    }

    @Operation(summary = "Update a role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<RoleResponse>> updateRole(
            @Parameter(description = "Role UUID") @PathVariable UUID id,
            @RequestBody UpdateRoleRequest request) {
        RoleResponse response = roleService.updateRole(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("Role updated successfully", response));
    }

    @Operation(summary = "Delete a role")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Role deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteRole(
            @Parameter(description = "Role UUID") @PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseWrapper.success("Role deleted successfully", null));
    }

}
