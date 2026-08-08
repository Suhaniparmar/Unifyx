package org.example.unifyx.repository;

import org.example.unifyx.Model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Integer> {
    
    // Get all quotes for a specific post
    @Query("SELECT q FROM Quote q WHERE q.post.postId = :postId ORDER BY q.createdAt DESC")
    List<Quote> findByPostId(@Param("postId") int postId);
    
    // Get quotes for a post sorted by trust score and price
    @Query("""
        SELECT q FROM Quote q
        LEFT JOIN q.worker w
        LEFT JOIN w.trustScore ts
        WHERE q.post.postId = :postId
        ORDER BY 
            COALESCE(ts.score, 0) DESC,
            q.price ASC
        """)
    List<Quote> findQuotesForPostSortedByTrustAndPrice(@Param("postId") int postId);
    
    // Get quotes from a specific worker for a post
    @Query("SELECT q FROM Quote q WHERE q.post.postId = :postId AND q.worker.workerId = :workerId")
    Optional<Quote> findByPostIdAndWorkerId(@Param("postId") int postId, @Param("workerId") int workerId);

    @Query("SELECT q FROM Quote q WHERE q.post.postId = :postId AND q.contractor.contractorId = :contractorId")
    Optional<Quote> findByPostIdAndContractorId(@Param("postId") int postId, @Param("contractorId") int contractorId);
    
    // Get all quotes by a worker
    List<Quote> findByWorker_WorkerId(int workerId);

    List<Quote> findByWorker_WorkerIdOrderByCreatedAtDesc(int workerId);

    List<Quote> findByContractor_ContractorIdOrderByCreatedAtDesc(int contractorId);

    void deleteByPost_PostId(int postId);

    // Get quotes with specific status
    List<Quote> findByStatusAndWorker_WorkerId(String status, int workerId);
}


