package org.s3m.userservice.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ApiResponse;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.user.dto.CreateUserRequest;
import org.s3m.userservice.user.dto.UpdateUserRequest;
import org.s3m.userservice.user.dto.UserResponse;
import org.s3m.userservice.user.service.UserService;
import org.s3m.userservice.usertenant.dto.CreateUserTenantRequest;
import org.s3m.userservice.usertenant.dto.UserTenantResponse;
import org.s3m.userservice.usertenant.service.UserTenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "User Management", description = "CRUD for users and user-tenant assignments")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(AppConstants.API_PREFIX + "/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserTenantService userTenantService;

    @Operation(summary = "Create a user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User created successfully", response));
    }

    @Operation(summary = "Get a user by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "User UUID") @PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @Operation(summary = "Get a user by username")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(
            @Parameter(description = "Username") @PathVariable String username) {
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @Operation(summary = "Get all users")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(@AuthenticationPrincipal Jwt jwt) {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", response));
    }

    @Operation(summary = "Update a user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "User UUID") @PathVariable UUID id,
            @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @Operation(summary = "Delete a user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "User deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "User UUID") @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(ApiResponse.success("User deleted successfully", null));
    }

    @Operation(summary = "Assign a user to a tenant")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User assigned to tenant"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User or tenant not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already assigned to tenant"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/user-tenants")
    public ResponseEntity<ApiResponse<UserTenantResponse>> assignUserToTenant(@RequestBody CreateUserTenantRequest request) {
        UserTenantResponse response = userTenantService.assignUserToTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User assigned to tenant successfully", response));
    }

    @DeleteMapping("/{id}/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> removeUserFromTenantByUserAndTenant(
            @Parameter(description = "User UUID") @PathVariable UUID id,
            @Parameter(description = "Tenant UUID") @PathVariable UUID tenantId) {
        userTenantService.removeUserFromTenantByUserAndTenant(id, tenantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("User removed from tenant successfully", null));
    }

}
