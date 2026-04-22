package org.s3m.userservice.usertenant.controller;

import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ApiResponse;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.usertenant.dto.UserTenantResponse;
import org.s3m.userservice.usertenant.service.UserTenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_PREFIX + "/user-tenants")
@AllArgsConstructor
public class UserTenantController {

    private final UserTenantService userTenantService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserTenantResponse>> getUserTenantById(@PathVariable UUID id) {
        UserTenantResponse response = userTenantService.getUserTenantById(id);
        return ResponseEntity.ok(ApiResponse.success("User-Tenant relationship retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserTenantResponse>>> getAllUserTenants() {
        List<UserTenantResponse> response = userTenantService.getAllUserTenants();
        return ResponseEntity.ok(ApiResponse.success("User-Tenant relationships retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeUserFromTenant(@PathVariable UUID id) {
        userTenantService.removeUserFromTenant(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("User removed from tenant successfully", null));
    }
}
