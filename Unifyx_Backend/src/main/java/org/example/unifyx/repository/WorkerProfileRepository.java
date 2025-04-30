package org.example.unifyx.repository;

import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.Model.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Integer> {
    // Custom queries can be added here if necessary
    WorkerProfile findByEmail(String email);

    @Query("SELECT w FROM WorkerProfile w WHERE LOWER(w.address) = LOWER(:address)")
    List<WorkerProfile> findByAddressIgnoreCase(@Param("address") String address);

}
