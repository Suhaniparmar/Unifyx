package org.example.unifyx.repository;

import org.example.unifyx.Model.Hire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HireRepository extends JpaRepository<Hire, Integer> {
    
    // Get hire record by post and worker
    @Query("SELECT h FROM Hire h WHERE h.post.postId = :postId AND h.worker.workerId = :workerId")
    Optional<Hire> findByPostIdAndWorkerId(@Param("postId") int postId, @Param("workerId") int workerId);
    
    // Get all active hires for a post
    @Query("SELECT h FROM Hire h WHERE h.post.postId = :postId AND h.status = 'ACTIVE'")
    List<Hire> findActiveHiresByPostId(@Param("postId") int postId);
    
    // Get all hires for a worker
    List<Hire> findByWorker_WorkerId(int workerId);

    void deleteByPost_PostId(int postId);
    
    // Get completed hires for a worker
    @Query("SELECT h FROM Hire h WHERE h.worker.workerId = :workerId AND h.status = 'COMPLETED'")
    List<Hire> findCompletedHiresByWorkerId(@Param("workerId") int workerId);
}


