package org.example.unifyx.Repository;

import org.example.unifyx.Model.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Integer> {
    // Custom queries can be added here if necessary
}
