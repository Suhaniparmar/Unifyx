package org.example.unifyx.repository;

import org.example.unifyx.Model.ContractorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractorProfileRepository extends JpaRepository<ContractorProfile, Integer> {
    // You can add custom queries here if necessary
}
