package org.s3m.userservice.user.controller;

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

@RestController
@RequestMapping(AppConstants.API_PREFIX + "/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserTenantService userTenantService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(@AuthenticationPrincipal Jwt jwt) {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable UUID id,
                                                                @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(ApiResponse.success("User deleted successfully", null));
    }

    @PostMapping("/user-tenants")
    public ResponseEntity<ApiResponse<UserTenantResponse>> assignUserToTenant(@RequestBody CreateUserTenantRequest request) {
        UserTenantResponse response = userTenantService.assignUserToTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User assigned to tenant successfully", response));
    }

    @GetMapping("/{id}/user-tenants")
    public ResponseEntity<ApiResponse<List<UserTenantResponse>>> getUserTenantsByUserId(@PathVariable UUID id) {
        List<UserTenantResponse> response = userTenantService.getUserTenantsByUserId(id);
        return ResponseEntity.ok(ApiResponse.success("User-Tenant relationships retrieved successfully", response));
    }

    @DeleteMapping("/{id}/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> removeUserFromTenantByUserAndTenant(@PathVariable UUID id,
                                                                                 @PathVariable UUID tenantId) {
        userTenantService.removeUserFromTenantByUserAndTenant(id, tenantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("User removed from tenant successfully", null));
    }

}

