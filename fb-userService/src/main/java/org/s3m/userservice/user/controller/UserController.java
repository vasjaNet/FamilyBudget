package org.s3m.userservice.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ResponseWrapper;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.user.dto.CreateUserRequest;
import org.s3m.userservice.user.dto.UpdateUserRequest;
import org.s3m.userservice.user.dto.UserResponse;
import org.s3m.userservice.user.dto.UserResponseBasic;
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
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseWrapper.success("User created successfully", response));
    }

    @Operation(summary = "Get a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<UserResponse>> getUserById(
            @Parameter(description = "User UUID") @PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ResponseWrapper.success("User retrieved successfully", response));
    }

    @Operation(summary = "Get a user by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<ResponseWrapper<UserResponse>> getUserByUsername(
            @Parameter(description = "Username") @PathVariable String username) {
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(ResponseWrapper.success("User retrieved successfully", response));
    }

    @Operation(summary = "Get all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<UserResponse>>> getAllUsers(@AuthenticationPrincipal Jwt jwt) {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(ResponseWrapper.success("Users retrieved successfully", response));
    }

    @GetMapping("/basic")
    public ResponseEntity<ResponseWrapper<List<UserResponseBasic>>> getAllUsersBasic(@AuthenticationPrincipal Jwt jwt) {
        List<UserResponseBasic> response = userService.getAllUsersBasic();
        return ResponseEntity.ok(ResponseWrapper.success("Users retrieved successfully", response));
    }

    @Operation(summary = "Update a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<UserResponse>> updateUser(
            @Parameter(description = "User UUID") @PathVariable UUID id,
            @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("User updated successfully", response));
    }

    @Operation(summary = "Delete a user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteUser(
            @Parameter(description = "User UUID") @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(ResponseWrapper.success("User deleted successfully", null));
    }

    @Operation(summary = "Assign a user to a tenant")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User assigned to tenant"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User or tenant not found"),
            @ApiResponse(responseCode = "409", description = "User already assigned to tenant"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/user-tenants")
    public ResponseEntity<ResponseWrapper<UserTenantResponse>> assignUserToTenant(@RequestBody CreateUserTenantRequest request) {
        UserTenantResponse response = userTenantService.assignUserToTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("User assigned to tenant successfully", response));
    }

    @DeleteMapping("/{id}/tenant/{tenantId}")
    public ResponseEntity<ResponseWrapper<Void>> removeUserFromTenantByUserAndTenant(
            @Parameter(description = "User UUID") @PathVariable UUID id,
            @Parameter(description = "Tenant UUID") @PathVariable UUID tenantId) {
        userTenantService.removeUserFromTenantByUserAndTenant(id, tenantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseWrapper.success("User removed from tenant successfully", null));
    }

}
