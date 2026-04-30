package org.s3m.userservice.tenant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ResponseWrapper;
import org.s3m.commonlib.util.AppConstants;
import org.s3m.userservice.tenant.dto.CreateTenantRequest;
import org.s3m.userservice.tenant.dto.TenantResponse;
import org.s3m.userservice.tenant.dto.TenantResponseBasic;
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

@Tag(name = "Tenant Management", description = "CRUD for tenants and user-tenant assignments")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(AppConstants.API_PREFIX + "/tenants")
@AllArgsConstructor
public class TenantController {

    private final TenantService tenantService;
    private final UserTenantService userTenantService;

    @Operation(summary = "Create a tenant")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tenant created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Tenant already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<TenantResponse>> createTenant(@RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("Tenant created successfully", response));
    }

    @Operation(summary = "Get a tenant by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tenant retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Tenant not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TenantResponse>> getTenantById(
            @Parameter(description = "Tenant UUID") @PathVariable UUID id) {
        TenantResponse response = tenantService.getTenantById(id);
        return ResponseEntity.ok(ResponseWrapper.success("Tenant retrieved successfully", response));
    }

    @Operation(summary = "Get a tenant by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tenant retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Tenant not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<ResponseWrapper<TenantResponse>> getTenantByName(
            @Parameter(description = "Tenant name") @PathVariable String name) {
        TenantResponse response = tenantService.getTenantByName(name);
        return ResponseEntity.ok(ResponseWrapper.success("Tenant retrieved successfully", response));
    }

    @Operation(summary = "Get all tenants")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tenants retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<TenantResponse>>> getAllTenants() {
        return ResponseEntity.ok(
                ResponseWrapper.success("Tenants retrieved successfully", tenantService.getAllTenants()));
    }

    @GetMapping("/basic")
    public ResponseEntity<ResponseWrapper<List<TenantResponseBasic>>> getAllTenantsBasic() {
        return ResponseEntity.ok(
                ResponseWrapper.success("Tenants retrieved successfully", tenantService.getAllTenantsBasic()));
    }

    @Operation(summary = "Update a tenant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tenant updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Tenant not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TenantResponse>> updateTenant(
            @Parameter(description = "Tenant UUID") @PathVariable UUID id,
            @RequestBody UpdateTenantRequest request) {
        TenantResponse response = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("Tenant updated successfully", response));
    }

    @Operation(summary = "Delete a tenant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tenant deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Tenant not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteTenant(
            @Parameter(description = "Tenant UUID") @PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseWrapper.success("Tenant deleted successfully", null));
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

    @DeleteMapping("/{tenantId}/user/{userId}")
    public ResponseEntity<ResponseWrapper<Void>> removeUserFromTenantByUserAndTenant(
            @Parameter(description = "User UUID") @PathVariable UUID userId,
            @Parameter(description = "Tenant UUID") @PathVariable UUID tenantId) {
        userTenantService.removeUserFromTenantByUserAndTenant(userId, tenantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseWrapper.success("User removed from tenant successfully", null));
    }

}
