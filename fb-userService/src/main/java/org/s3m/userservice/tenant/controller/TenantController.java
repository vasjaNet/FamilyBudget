package org.s3m.userservice.tenant.controller;

import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ApiResponse;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.tenant.dto.CreateTenantRequest;
import org.s3m.userservice.tenant.dto.TenantResponse;
import org.s3m.userservice.tenant.dto.UpdateTenantRequest;
import org.s3m.userservice.tenant.service.TenantService;
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

    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
            @RequestBody CreateTenantRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        TenantResponse response = tenantService.createTenant(request, userId);
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
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
            @PathVariable UUID id,
            @RequestBody UpdateTenantRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        TenantResponse response = tenantService.updateTenant(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String userId) {
        tenantService.deleteTenant(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Tenant deleted successfully", null));
    }

}
