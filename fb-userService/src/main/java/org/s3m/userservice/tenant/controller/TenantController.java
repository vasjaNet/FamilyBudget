package org.s3m.userservice.tenant.controller;

import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ApiResponse;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.tenant.dto.CreateTenantRequest;
import org.s3m.userservice.tenant.dto.TenantResponse;
import org.s3m.userservice.tenant.dto.UpdateTenantRequest;
import org.s3m.userservice.tenant.service.TenantService;
import org.s3m.userservice.usertenant.dto.CreateUserTenantRequest;
import org.s3m.userservice.usertenant.dto.UserTenantResponse;
import org.s3m.userservice.usertenant.service.UserTenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_PREFIX + "/tenants")
@AllArgsConstructor
public class TenantController {

    private final TenantService tenantService;
    private final UserTenantService userTenantService;

    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(@RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tenant created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenantById(@PathVariable UUID id) {
        TenantResponse response = tenantService.getTenantById(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", response));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenantByName(@PathVariable String name) {
        TenantResponse response = tenantService.getTenantByName(name);
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantResponse>>> getAllTenants() {
        List<TenantResponse> response = tenantService.getAllTenants();
        return ResponseEntity.ok(ApiResponse.success("Tenants retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(@PathVariable UUID id,
                                                                    @RequestBody UpdateTenantRequest request) {
        TenantResponse response = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Tenant deleted successfully", null));
    }

    @PostMapping("/user-tenants")
    public ResponseEntity<ApiResponse<UserTenantResponse>> assignUserToTenant(@RequestBody CreateUserTenantRequest request) {
        UserTenantResponse response = userTenantService.assignUserToTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User assigned to tenant successfully", response));
    }

    @GetMapping("/{tenantId}/user-tenants")
    public ResponseEntity<ApiResponse<List<UserTenantResponse>>> getUserTenantsByTenantId(@PathVariable UUID tenantId) {
        List<UserTenantResponse> response = userTenantService.getUserTenantsByTenantId(tenantId);
        return ResponseEntity.ok(ApiResponse.success("User-Tenant relationships retrieved successfully", response));
    }

    @DeleteMapping("/{tenantId}/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeUserFromTenantByUserAndTenant(@PathVariable UUID userId,
                                                                                 @PathVariable UUID tenantId) {
        userTenantService.removeUserFromTenantByUserAndTenant(userId, tenantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("User removed from tenant successfully", null));
    }

}
