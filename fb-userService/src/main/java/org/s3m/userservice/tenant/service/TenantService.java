package org.s3m.userservice.tenant.service;

import lombok.AllArgsConstructor;
import org.s3m.userservice.tenant.dto.CreateTenantRequest;
import org.s3m.userservice.tenant.dto.TenantResponse;
import org.s3m.userservice.tenant.dto.TenantResponseBasic;
import org.s3m.userservice.tenant.dto.UpdateTenantRequest;
import org.s3m.userservice.tenant.entity.Tenant;
import org.s3m.userservice.tenant.mapper.TenantMapper;
import org.s3m.userservice.tenant.repository.TenantRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @CachePut(value = "tenants", key = "#result.id")
    public TenantResponse createTenant(CreateTenantRequest request) {
        if (tenantRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Tenant with name '" + request.name() + "' already exists");
        }

        Tenant tenant = tenantMapper.mapToEntity(request);

        Tenant savedTenant = tenantRepository.save(tenant);
        return tenantMapper.mapToResponse(savedTenant);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tenants", key = "#tenantId")
    public TenantResponse getTenantById(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        return tenantMapper.mapToResponse(tenant);
    }

    @Transactional(readOnly = true)
    public TenantResponse getTenantByName(String name) {
        Tenant tenant = tenantRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        return tenantMapper.mapToResponse(tenant);
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> getAllTenants() {
        return tenantMapper.mapToResponseList(tenantRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<TenantResponseBasic> getAllTenantsBasic() {
        return tenantMapper.mapToResponseBasicList(tenantRepository.findAll());
    }

    @CacheEvict(value = "tenants", key = "#tenantId")
    public TenantResponse updateTenant(UUID tenantId, UpdateTenantRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

        if (!tenant.getName().equals(request.name()) && tenantRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Tenant with name '" + request.name() + "' already exists");
        }

        tenantMapper.mapToEntity(request, tenant);

        Tenant updatedTenant = tenantRepository.save(tenant);
        return tenantMapper.mapToResponse(updatedTenant);
    }

    @CacheEvict(value = "tenants", key = "#tenantId")
    public void deleteTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        tenantRepository.delete(tenant);
    }

}
