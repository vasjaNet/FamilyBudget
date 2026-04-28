package org.s3m.userservice.tenant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.s3m.commonlib.config.ApiResponse;
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tenant created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Tenant already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(@RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tenant created successfully", response));
    }

    @Operation(summary = "Get a tenant by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenant retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenantById(
            @Parameter(description = "Tenant UUID") @PathVariable UUID id) {
        TenantResponse response = tenantService.getTenantById(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", response));
    }

    @Operation(summary = "Get a tenant by name")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenant retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenantByName(
            @Parameter(description = "Tenant name") @PathVariable String name) {
        TenantResponse response = tenantService.getTenantByName(name);
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", response));
    }

    @Operation(summary = "Get all tenants")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenants retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantResponse>>> getAllTenants() {
        return ResponseEntity.ok(
                ApiResponse.success("Tenants retrieved successfully", tenantService.getAllTenants()));
    }

    @GetMapping("/basic")
    public ResponseEntity<ApiResponse<List<TenantResponseBasic>>> getAllTenantsBasic() {
        return ResponseEntity.ok(
                ApiResponse.success("Tenants retrieved successfully", tenantService.getAllTenantsBasic()));
    }

    @Operation(summary = "Update a tenant")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tenant updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
            @Parameter(description = "Tenant UUID") @PathVariable UUID id,
            @RequestBody UpdateTenantRequest request) {
        TenantResponse response = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", response));
    }

    @Operation(summary = "Delete a tenant")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Tenant deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(
            @Parameter(description = "Tenant UUID") @PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Tenant deleted successfully", null));
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

    @DeleteMapping("/{tenantId}/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeUserFromTenantByUserAndTenant(
            @Parameter(description = "User UUID") @PathVariable UUID userId,
            @Parameter(description = "Tenant UUID") @PathVariable UUID tenantId) {
        userTenantService.removeUserFromTenantByUserAndTenant(userId, tenantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("User removed from tenant successfully", null));
    }

}
