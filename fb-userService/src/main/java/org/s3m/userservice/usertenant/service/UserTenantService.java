package org.s3m.userservice.usertenant.service;

import lombok.AllArgsConstructor;
import org.s3m.userservice.role.entity.Role;
import org.s3m.userservice.role.repository.RoleRepository;
import org.s3m.userservice.tenant.entity.Tenant;
import org.s3m.userservice.tenant.repository.TenantRepository;
import org.s3m.userservice.usertenant.dto.CreateUserTenantRequest;
import org.s3m.userservice.usertenant.dto.UserTenantResponse;
import org.s3m.userservice.usertenant.entity.UserTenant;
import org.s3m.userservice.usertenant.mapper.UserTenantMapper;
import org.s3m.userservice.usertenant.repository.UserTenantRepository;
import org.s3m.userservice.user.entity.User;
import org.s3m.userservice.user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class UserTenantService {

    private final UserTenantRepository userTenantRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final UserTenantMapper userTenantMapper;

    public UserTenantResponse assignUserToTenant(CreateUserTenantRequest request) {
        if (userTenantRepository.existsByUserIdAndTenantId(request.userId(), request.tenantId())) {
            throw new IllegalArgumentException("User is already assigned to this tenant");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        UserTenant userTenant = UserTenant.builder()
                .user(user)
                .tenant(tenant)
                .userRole(role)
                .build();

        UserTenant savedUserTenant = userTenantRepository.save(userTenant);
        return userTenantMapper.mapToResponse(savedUserTenant);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "userTenants", key = "#userTenantId")
    public UserTenantResponse getUserTenantById(UUID userTenantId) {
        UserTenant userTenant = userTenantRepository.findById(userTenantId)
                .orElseThrow(() -> new IllegalArgumentException("User-Tenant relationship not found"));
        return userTenantMapper.mapToResponse(userTenant);
    }

    @Transactional(readOnly = true)
    public List<UserTenantResponse> getAllUserTenants() {
        return userTenantRepository.findAll().stream()
                .map(userTenantMapper::mapToResponse)
                .toList();
    }

    @CacheEvict(value = "userTenants", key = "#userTenantId")
    public void removeUserFromTenant(UUID userTenantId) {
        UserTenant userTenant = userTenantRepository.findById(userTenantId)
                .orElseThrow(() -> new IllegalArgumentException("User-Tenant relationship not found"));
        userTenantRepository.delete(userTenant);
    }

    @CacheEvict(value = "userTenants", allEntries = true)
    public void removeUserFromTenantByUserAndTenant(UUID userId, UUID tenantId) {
        UserTenant userTenant = userTenantRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("User-Tenant relationship not found"));
        userTenantRepository.delete(userTenant);
    }

}
