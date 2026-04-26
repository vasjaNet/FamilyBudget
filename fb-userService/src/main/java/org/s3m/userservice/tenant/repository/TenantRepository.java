package org.s3m.userservice.tenant.repository;

import org.s3m.userservice.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    @EntityGraph(attributePaths = {"userTenants", "userTenants.user", "userTenants.userRole"})
    List<Tenant> findAll();

    @EntityGraph(attributePaths = {"userTenants", "userTenants.user", "userTenants.userRole"})
    Optional<Tenant> findById(Long id);

    Optional<Tenant> findByName(String name);

    boolean existsByName(String name);

}
