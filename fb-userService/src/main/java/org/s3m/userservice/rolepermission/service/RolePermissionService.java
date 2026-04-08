package org.s3m.userservice.rolepermission.service;

import lombok.AllArgsConstructor;
import org.s3m.userservice.permission.entity.Permission;
import org.s3m.userservice.permission.repository.PermissionRepository;
import org.s3m.userservice.role.entity.Role;
import org.s3m.userservice.role.repository.RoleRepository;
import org.s3m.userservice.rolepermission.dto.CreateRolePermissionRequest;
import org.s3m.userservice.rolepermission.dto.RolePermissionResponse;
import org.s3m.userservice.rolepermission.entity.RolePermission;
import org.s3m.userservice.rolepermission.mapper.RolePermissionMapper;
import org.s3m.userservice.rolepermission.repository.RolePermissionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionMapper rolePermissionMapper;

    public RolePermissionResponse assignPermissionToRole(CreateRolePermissionRequest request, String changedBy) {
        if (rolePermissionRepository.existsByRoleIdAndPermissionId(request.roleId(), request.permissionId())) {
            throw new IllegalArgumentException("Permission is already assigned to this role");
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        Permission permission = permissionRepository.findById(request.permissionId())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

        RolePermission rolePermission = RolePermission.builder()
                .role(role)
                .permission(permission)
                .createdBy(changedBy)
                .updatedBy(changedBy)
                .build();

        RolePermission savedRolePermission = rolePermissionRepository.save(rolePermission);
        return rolePermissionMapper.mapToResponse(savedRolePermission);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "rolePermissions", key = "#rolePermissionId")
    public RolePermissionResponse getRolePermissionById(UUID rolePermissionId) {
        RolePermission rolePermission = rolePermissionRepository.findById(rolePermissionId)
                .orElseThrow(() -> new IllegalArgumentException("Role-Permission relationship not found"));
        return rolePermissionMapper.mapToResponse(rolePermission);
    }

    @Transactional(readOnly = true)
    public RolePermissionResponse getRolePermissionByRoleAndPermission(UUID roleId, UUID permissionId) {
        RolePermission rolePermission = rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Role-Permission relationship not found"));
        return rolePermissionMapper.mapToResponse(rolePermission);
    }

    @Transactional(readOnly = true)
    public List<RolePermissionResponse> getRolePermissionsByRoleId(UUID roleId) {
        return rolePermissionRepository.findByRoleId(roleId).stream()
                .map(rolePermissionMapper::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RolePermissionResponse> getRolePermissionsByPermissionId(UUID permissionId) {
        return rolePermissionRepository.findByPermissionId(permissionId).stream()
                .map(rolePermissionMapper::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RolePermissionResponse> getAllRolePermissions() {
        return rolePermissionRepository.findAll().stream()
                .map(rolePermissionMapper::mapToResponse)
                .toList();
    }

    @CacheEvict(value = "rolePermissions", key = "#rolePermissionId")
    public void removePermissionFromRole(UUID rolePermissionId, String changedBy) {
        RolePermission rolePermission = rolePermissionRepository.findById(rolePermissionId)
                .orElseThrow(() -> new IllegalArgumentException("Role-Permission relationship not found"));
        rolePermissionRepository.delete(rolePermission);
    }

    @CacheEvict(value = "rolePermissions", allEntries = true)
    public void removePermissionFromRoleByIds(UUID roleId, UUID permissionId, String changedBy) {
        RolePermission rolePermission = rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Role-Permission relationship not found"));
        rolePermissionRepository.delete(rolePermission);
    }

}