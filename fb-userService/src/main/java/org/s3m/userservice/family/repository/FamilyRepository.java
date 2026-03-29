package org.s3m.userservice.family.repository;

import org.s3m.userservice.family.entity.Family;
import org.s3m.userservice.family.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID> {
    List<Family> findByOwnerId(UUID ownerId);

    @Query("SELECT f FROM Family f WHERE f.id IN " +
           "(SELECT fm.family.id FROM FamilyMember fm WHERE fm.userId = :userId)")
    List<Family> findByUserId(@Param("userId") UUID userId);

    Optional<Family> findByIdAndOwnerId(UUID id, UUID ownerId);
}