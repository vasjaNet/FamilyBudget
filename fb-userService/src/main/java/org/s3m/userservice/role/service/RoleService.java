package org.s3m.userservice.role.service;

import lombok.AllArgsConstructor;
import org.s3m.userservice.role.dto.CreateRoleRequest;
import org.s3m.userservice.role.dto.RoleResponse;
import org.s3m.userservice.role.dto.RoleResponseBasic;
import org.s3m.userservice.role.dto.UpdateRoleRequest;
import org.s3m.userservice.role.entity.Role;
import org.s3m.userservice.role.mapper.RoleMapper;
import org.s3m.userservice.role.repository.RoleRepository;
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
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @CachePut(value = "roles", key = "#result.id")
    public RoleResponse createRole(CreateRoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Role with name '" + request.name() + "' already exists");
        }

        Role role = roleMapper.mapToEntity(request);

        Role savedRole = roleRepository.save(role);
        return roleMapper.mapToResponse(savedRole);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "#roleId", unless = "#result == null")
    public RoleResponse getRoleById(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));
        return roleMapper.mapToResponse(role);
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with name: " + name));
        return roleMapper.mapToResponse(role);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleMapper.mapToResponseList(roleRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<RoleResponseBasic> getAllRolesBasic() {
        return roleMapper.mapToResponseBasicList(roleRepository.findAll());
    }

    @CacheEvict(value = "roles", key = "#roleId")
    public RoleResponse updateRole(UUID roleId, UpdateRoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        if (!role.getName().equals(request.name()) && roleRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Role with name '" + request.name() + "' already exists");
        }

        roleMapper.mapToEntity(request, role);

        Role updatedRole = roleRepository.save(role);
        return roleMapper.mapToResponse(updatedRole);
    }

    @CacheEvict(value = "roles", key = "#roleId")
    public void deleteRole(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));
        roleRepository.delete(role);
    }

}
