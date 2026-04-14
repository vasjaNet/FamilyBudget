package org.s3m.userservice.rolepermission.repository;

import org.s3m.userservice.rolepermission.entity.RolePermission;
import org.s3m.userservice.rolepermission.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {

    Optional<RolePermission> findById(RolePermissionId id);

    List<RolePermission> findById_RoleId(UUID roleId);

    List<RolePermission> findById_PermissionId(UUID permissionId);

    boolean existsById(RolePermissionId id);

    void deleteById(RolePermissionId id);

}