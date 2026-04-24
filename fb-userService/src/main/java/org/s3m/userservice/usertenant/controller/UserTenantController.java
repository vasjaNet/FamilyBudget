package org.s3m.userservice.usertenant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "User-Tenant Relations", description = "Query and remove user-tenant membership records")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(AppConstants.API_PREFIX + "/user-tenants")
@AllArgsConstructor
public class UserTenantController {

    private final UserTenantService userTenantService;

    @Operation(summary = "Get a user-tenant record by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Record retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Record not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserTenantResponse>> getUserTenantById(
            @Parameter(description = "User-Tenant record UUID") @PathVariable UUID id) {
        UserTenantResponse response = userTenantService.getUserTenantById(id);
        return ResponseEntity.ok(ApiResponse.success("User-Tenant relationship retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserTenantResponse>>> getAllUserTenants() {
        List<UserTenantResponse> response = userTenantService.getAllUserTenants();
        return ResponseEntity.ok(ApiResponse.success("User-Tenant relationships retrieved successfully", response));
    }

    @Operation(summary = "Remove a user from a tenant by record ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "User removed from tenant"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Record not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeUserFromTenant(
            @Parameter(description = "User-Tenant record UUID") @PathVariable UUID id) {
        userTenantService.removeUserFromTenant(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("User removed from tenant successfully", null));
    }
}
