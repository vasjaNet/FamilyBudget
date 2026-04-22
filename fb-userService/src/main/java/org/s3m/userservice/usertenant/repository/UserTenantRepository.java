package org.s3m.userservice.usertenant.repository;

import org.s3m.userservice.usertenant.entity.UserTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTenantRepository extends JpaRepository<UserTenant, UUID> {

    Optional<UserTenant> findByUserIdAndTenantId(UUID userId, UUID tenantId);

    List<UserTenant> findByUserId(UUID userId);

    List<UserTenant> findByTenantId(UUID tenantId);

    boolean existsByUserIdAndTenantId(UUID userId, UUID tenantId);

}
