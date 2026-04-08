package org.s3m.userservice.permission.service;

import lombok.AllArgsConstructor;
import org.s3m.userservice.permission.dto.CreatePermissionRequest;
import org.s3m.userservice.permission.dto.PermissionResponse;
import org.s3m.userservice.permission.dto.UpdatePermissionRequest;
import org.s3m.userservice.permission.entity.Permission;
import org.s3m.userservice.permission.mapper.PermissionMapper;
import org.s3m.userservice.permission.repository.PermissionRepository;
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
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @CachePut(value = "permissions", key = "#result.id")
    public PermissionResponse createPermission(CreatePermissionRequest request, String changedBy) {
        if (permissionRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Permission with name '" + request.name() + "' already exists");
        }

        Permission permission = permissionMapper.mapToEntity(request);
        permission.setCreatedBy(changedBy);
        permission.setUpdatedBy(changedBy);

        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.mapToResponse(savedPermission);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "permissions", key = "#permissionId", unless = "#result == null")
    public PermissionResponse getPermissionById(UUID permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));
        return permissionMapper.mapToResponse(permission);
    }

    @Transactional(readOnly = true)
    public PermissionResponse getPermissionByName(String name) {
        Permission permission = permissionRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with name: " + name));
        return permissionMapper.mapToResponse(permission);
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByResource(String resource) {
        return permissionRepository.findByResource(resource).stream()
                .map(permissionMapper::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByAction(String action) {
        return permissionRepository.findByAction(action).stream()
                .map(permissionMapper::mapToResponse)
                .toList();
    }

    @CacheEvict(value = "permissions", key = "#permissionId")
    public PermissionResponse updatePermission(UUID permissionId, UpdatePermissionRequest request, String changedBy) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        if (!permission.getName().equals(request.name()) && permissionRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Permission with name '" + request.name() + "' already exists");
        }

        permissionMapper.mapToEntity(request, permission);
        permission.setUpdatedBy(changedBy);

        Permission updatedPermission = permissionRepository.save(permission);
        return permissionMapper.mapToResponse(updatedPermission);
    }

    @CacheEvict(value = "permissions", key = "#permissionId")
    public void deletePermission(UUID permissionId, String changedBy) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));
        permissionRepository.delete(permission);
    }

}