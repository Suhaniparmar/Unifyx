package org.example.unifyx.repository;

import org.example.unifyx.Model.ContractorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractorProfileRepository extends JpaRepository<ContractorProfile, Integer> {
    // You can add custom queries here if necessary
    @Query("SELECT c FROM ContractorProfile c WHERE LOWER(c.siteAddress) = LOWER(:siteAddress)")
    List<ContractorProfile> findBySiteAddressIgnoreCase(@Param("siteAddress") String siteAddress);

}
