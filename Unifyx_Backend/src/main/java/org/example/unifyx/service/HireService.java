package org.example.unifyx.service;

import org.example.unifyx.Model.*;
import org.example.unifyx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for managing hire agreements between owners and workers
 */
@Service
@Transactional
public class HireService {
    
    @Autowired
    private HireRepository hireRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private WorkerProfileRepository workerProfileRepository;
    
    @Autowired
    private QuoteRepository quoteRepository;
    
    @Autowired
    private TrustScoreRepository trustScoreRepository;
    
    /**
     * Create a hire agreement (owner selects a worker)
     */
    public Hire createHire(int postId, int workerId, Double agreedPrice) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        WorkerProfile worker = workerProfileRepository.findById(workerId)
            .orElseThrow(() -> new IllegalArgumentException("Worker not found: " + workerId));
        
        Hire hire = new Hire(post, worker, agreedPrice);
        Hire savedHire = hireRepository.save(hire);
        
        // Update post status to "HIRED"
        post.setStatus("HIRED");
        postRepository.save(post);
        
        // Update associated quote status to ACCEPTED if exists
        Optional<Quote> quote = quoteRepository.findByPostIdAndWorkerId(postId, workerId);
        if (quote.isPresent()) {
            Quote q = quote.get();
            q.setStatus("ACCEPTED");
            quoteRepository.save(q);
        }
        
        return savedHire;
    }
    
    /**
     * Get a specific hire by ID
     */
    public Hire getHireById(int hireId) {
        return hireRepository.findById(hireId)
            .orElseThrow(() -> new IllegalArgumentException("Hire not found: " + hireId));
    }
    
    /**
     * Get active hires for a post
     */
    public List<Hire> getActiveHiresForPost(int postId) {
        return hireRepository.findActiveHiresByPostId(postId);
    }
    
    /**
     * Get all hires for a worker
     */
    public List<Hire> getHiresForWorker(int workerId) {
        return hireRepository.findByWorker_WorkerId(workerId);
    }
    
    /**
     * Mark a hire as completed
     */
    public Hire completeHire(int hireId) {
        Hire hire = getHireById(hireId);
        hire.setStatus("COMPLETED");
        hire.setCompletedAt(LocalDateTime.now());
        
        Hire savedHire = hireRepository.save(hire);
        
        // Update worker's trust score based on completion
        updateWorkerTrustScoreOnCompletion(hire.getWorker());
        
        return savedHire;
    }
    
    /**
     * Owner rates and reviews a completed hire
     */
    public Hire rateHire(int hireId, Double rating, String review) {
        Hire hire = getHireById(hireId);
        
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        hire.setOwnerRating(rating);
        hire.setOwnerReview(review);
        
        Hire savedHire = hireRepository.save(hire);
        
        // Update trust score with owner's rating
        updateTrustScoreWithRating(hire.getWorker(), rating);
        
        return savedHire;
    }
    
    /**
     * Cancel a hire agreement
     */
    public Hire cancelHire(int hireId) {
        Hire hire = getHireById(hireId);
        hire.setStatus("CANCELLED");
        return hireRepository.save(hire);
    }
    
    /**
     * Update worker's trust score when a job is completed
     */
    private void updateWorkerTrustScoreOnCompletion(WorkerProfile worker) {
        TrustScore trustScore = trustScoreRepository.findByWorker_WorkerId(worker.getWorkerId())
            .orElseGet(() -> {
                TrustScore newScore = new TrustScore(worker);
                return trustScoreRepository.save(newScore);
            });
        
        // Increment completed jobs
        trustScore.setTotalJobsCompleted(trustScore.getTotalJobsCompleted() + 1);
        
        // Update completion rate
        List<Hire> workerHires = hireRepository.findByWorker_WorkerId(worker.getWorkerId());
        long completedCount = workerHires.stream()
            .filter(h -> "COMPLETED".equals(h.getStatus()))
            .count();
        
        double completionRate = workerHires.isEmpty() ? 0.0 : (completedCount * 100.0 / workerHires.size());
        trustScore.setCompletionRate(completionRate);
        
        // Recalculate overall trust score
        double newScore = trustScore.calculateTrustScore();
        trustScore.setScore(newScore);
        
        trustScoreRepository.save(trustScore);
    }
    
    /**
     * Update trust score with owner's rating
     */
    private void updateTrustScoreWithRating(WorkerProfile worker, Double rating) {
        TrustScore trustScore = trustScoreRepository.findByWorker_WorkerId(worker.getWorkerId())
            .orElseGet(() -> {
                TrustScore newScore = new TrustScore(worker);
                return trustScoreRepository.save(newScore);
            });
        
        // Update rating average
        List<Hire> workerHires = hireRepository.findByWorker_WorkerId(worker.getWorkerId());
        double totalRating = workerHires.stream()
            .filter(h -> h.getOwnerRating() != null)
            .mapToDouble(Hire::getOwnerRating)
            .sum() + rating;
        
        long ratedCount = workerHires.stream()
            .filter(h -> h.getOwnerRating() != null)
            .count() + 1;
        
        double ratingAvg = totalRating / ratedCount;
        trustScore.setRatingAvg(ratingAvg);
        
        // Recalculate overall trust score
        double newScore = trustScore.calculateTrustScore();
        trustScore.setScore(newScore);
        
        trustScoreRepository.save(trustScore);
    }
}

