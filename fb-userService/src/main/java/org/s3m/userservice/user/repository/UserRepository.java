package org.s3m.userservice.user.repository;

import org.jspecify.annotations.NonNull;
import org.s3m.userservice.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Override
    @EntityGraph(attributePaths = {"userTenants", "userTenants.tenant", "userTenants.userRole"})
    List<User> findAll();

    @Override
    @EntityGraph(attributePaths = {"userTenants", "userTenants.tenant", "userTenants.userRole"})
    Optional<User> findById(@NonNull UUID id);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

