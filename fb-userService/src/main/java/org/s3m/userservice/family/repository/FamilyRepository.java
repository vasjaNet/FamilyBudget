package org.s3m.userservice.family.repository;

import org.s3m.userservice.family.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID> {
    List<Family> findByOwnerId(UUID ownerId);

    @Query("SELECT f FROM Family f WHERE f.id IN " +
           "(SELECT fm.familyId FROM FamilyMember fm WHERE fm.userId = :userId)")
    List<Family> findByUserId(UUID userId);

    Optional<Family> findByIdAndOwnerId(UUID id, UUID ownerId);
}