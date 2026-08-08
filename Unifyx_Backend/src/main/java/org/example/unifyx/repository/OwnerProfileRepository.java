package org.example.unifyx.repository;

import org.example.unifyx.Model.OwnerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerProfileRepository extends JpaRepository<OwnerProfile, Integer> {
    Optional<OwnerProfile> findTopByEmailIgnoreCaseOrderByOwnerIdDesc(String email);
}
