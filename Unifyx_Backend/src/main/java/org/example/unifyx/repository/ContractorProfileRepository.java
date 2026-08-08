package org.example.unifyx.repository;

import org.example.unifyx.Model.ContractorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractorProfileRepository extends JpaRepository<ContractorProfile, Integer> {
    Optional<ContractorProfile> findTopByEmailIgnoreCaseOrderByContractorIdDesc(String email);

    @Query("SELECT c FROM ContractorProfile c WHERE LOWER(c.address) = LOWER(:address)")
    List<ContractorProfile> findByAddressIgnoreCase(@Param("address") String address);

}
