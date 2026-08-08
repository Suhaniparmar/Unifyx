package org.example.unifyx.repository;

import org.example.unifyx.Model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {
    
    // Get all recommendations for a post, sorted by rank
    @Query("SELECT r FROM Recommendation r WHERE r.post.postId = :postId ORDER BY r.rank ASC")
    List<Recommendation> findByPostIdOrderByRank(@Param("postId") int postId);
    
    // Get top N recommendations for a post
    @Query(value = "SELECT r FROM Recommendation r WHERE r.post.postId = :postId ORDER BY r.score DESC LIMIT :limit")
    List<Recommendation> findTopRecommendationsForPost(@Param("postId") int postId, @Param("limit") int limit);
    
    // Check if a recommendation exists for a post and worker
    Optional<Recommendation> findByPost_PostIdAndWorker_WorkerId(int postId, int workerId);
    
    // Get all recommendations for a worker
    List<Recommendation> findByWorker_WorkerId(int workerId);
    
    // Get contacted recommendations for a post
    @Query("SELECT r FROM Recommendation r WHERE r.post.postId = :postId AND r.isContacted = true ORDER BY r.rank ASC")
    List<Recommendation> findContactedRecommendationsForPost(@Param("postId") int postId);

    void deleteByPost_PostId(int postId);
}

