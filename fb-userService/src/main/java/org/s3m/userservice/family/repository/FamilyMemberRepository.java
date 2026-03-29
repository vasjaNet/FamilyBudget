package org.s3m.userservice.family.repository;

import org.s3m.userservice.family.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {
    List<FamilyMember> findByFamilyId(UUID familyId);

    Optional<FamilyMember> findByFamilyIdAndUserId(UUID familyId, UUID userId);

    boolean existsByFamilyIdAndUserId(UUID familyId, UUID userId);

    @Query("SELECT fm FROM FamilyMember fm WHERE fm.family.id = :familyId AND fm.userId = :userId AND fm.role = :role")
    Optional<FamilyMember> findByFamilyIdAndUserIdAndRole(UUID familyId, UUID userId, FamilyMember.FamilyRole role);

    @Query("SELECT fm FROM FamilyMember fm WHERE fm.family.id = :familyId AND fm.userId = :userId AND fm.role IN ('OWNER', 'FULL_ACCESS')")
    List<FamilyMember> findFullAccessMembers(UUID familyId, UUID userId);

    void deleteByFamilyIdAndUserId(UUID familyId, UUID userId);

    void deleteByFamilyId(UUID familyId);
}