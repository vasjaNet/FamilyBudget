package org.s3m.userservice.rolepermission.repository;

import org.s3m.userservice.rolepermission.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    Optional<RolePermission> findByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

    List<RolePermission> findByRoleId(UUID roleId);

    List<RolePermission> findByPermissionId(UUID permissionId);

    boolean existsByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

    void deleteByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

}