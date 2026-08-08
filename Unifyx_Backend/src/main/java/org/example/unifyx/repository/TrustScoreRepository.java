package org.example.unifyx.repository;

import org.example.unifyx.Model.TrustScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrustScoreRepository extends JpaRepository<TrustScore, Integer> {
    
    // Find trust score by worker ID
    Optional<TrustScore> findByWorker_WorkerId(int workerId);
}

