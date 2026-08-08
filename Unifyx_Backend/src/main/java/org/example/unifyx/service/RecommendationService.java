package org.example.unifyx.service;

import org.example.unifyx.Model.*;
import org.example.unifyx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating worker recommendations based on skill match, trust score, and location.
 * Uses a weighted scoring algorithm to rank workers for a given job post.
 */
@Service
@Transactional
public class RecommendationService {
    
    @Autowired
    private RecommendationRepository recommendationRepository;
    
    @Autowired
    private WorkerProfileRepository workerProfileRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private TrustScoreRepository trustScoreRepository;
    
    // Matching parameters
    private static final double DEFAULT_SEARCH_RADIUS_KM = 15.0;
    private static final int MAX_RECOMMENDATIONS = 5;
    
    /**
     * Generate recommendations for a newly created post
     * Algorithm: Filter by skill → Sort by trust score, distance, completion rate → Return top 5
     */
    @Transactional
    public List<Recommendation> generateRecommendationsForPost(int postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> 
            new IllegalArgumentException("Post not found: " + postId));
        
        List<WorkerProfile> allWorkers = workerProfileRepository.findAll();
        
        // Filter and score workers
        List<WorkerScoreDto> scoredWorkers = allWorkers.stream()
            .filter(w -> matchesJobCategory(w, post.getWorkerCategory()))
            .filter(w -> isWithinSearchRadius(w, post, DEFAULT_SEARCH_RADIUS_KM))
            .map(w -> scoreWorker(w, post))
            .sorted((a, b) -> Double.compare(b.score, a.score)) // Descending by score
            .limit(MAX_RECOMMENDATIONS)
            .collect(Collectors.toList());
        
        // Save recommendations to database
        List<Recommendation> savedRecommendations = new ArrayList<>();
        for (int i = 0; i < scoredWorkers.size(); i++) {
            WorkerScoreDto dto = scoredWorkers.get(i);
            Recommendation recommendation = new Recommendation(
                post,
                dto.worker,
                dto.score,
                dto.skillMatchScore,
                dto.distance,
                i + 1 // Rank 1-5
            );
            savedRecommendations.add(recommendationRepository.save(recommendation));
        }
        
        return savedRecommendations;
    }
    
    /**
     * Check if worker's categories match the job
     */
    private boolean matchesJobCategory(WorkerProfile worker, String jobCategory) {
        if (worker.getCategories() == null || jobCategory == null) {
            return true; // Allow if no category filtering
        }
        return worker.getCategories().stream()
            .anyMatch(cat -> cat.equalsIgnoreCase(jobCategory));
    }
    
    /**
     * Check if worker is within search radius of job location
     */
    private boolean isWithinSearchRadius(WorkerProfile worker, Post post, double radiusKm) {
        // If location data missing, include by default
        if (worker.getLatitude() == null || post.getLatitude() == null) {
            return true;
        }
        
        double distance = calculateDistance(
            worker.getLatitude(), worker.getLongitude(),
            post.getLatitude(), post.getLongitude()
        );
        
        return distance <= radiusKm;
    }
    
    /**
     * Calculate distance between two coordinates using Haversine formula (km)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Radius in kilometers
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
    
    /**
     * Score a worker based on multiple factors:
     * - Trust score (40%)
     * - Distance (15%)
     * - Skill match (35%)
     * - Completion rate (10%)
     */
    private WorkerScoreDto scoreWorker(WorkerProfile worker, Post post) {
        double trustScore = getTrustScore(worker);
        double skillMatchScore = getSkillMatchScore(worker, post);
        double distance = calculateDistance(
            worker.getLatitude() != null ? worker.getLatitude() : 0,
            worker.getLongitude() != null ? worker.getLongitude() : 0,
            post.getLatitude() != null ? post.getLatitude() : 0,
            post.getLongitude() != null ? post.getLongitude() : 0
        );
        
        double completionRate = getCompletionRate(worker);
        
        // Weighted scoring algorithm
        double score = (trustScore * 0.40) +
                       (skillMatchScore * 0.35) +
                       (100 - Math.min(distance, 100)) * 0.15 + // Inverse distance scoring
                       (completionRate * 0.10);
        
        return new WorkerScoreDto(worker, score, skillMatchScore, distance);
    }
    
    /**
     * Get normalized trust score (0-10) for a worker
     */
    private double getTrustScore(WorkerProfile worker) {
        return trustScoreRepository.findByWorker_WorkerId(worker.getWorkerId())
            .map(TrustScore::getScore)
            .orElse(5.0); // Default base score
    }
    
    /**
     * Get skill match score (0-100) based on category overlap
     */
    private double getSkillMatchScore(WorkerProfile worker, Post post) {
        if (worker.getCategories() == null || post.getWorkerCategory() == null) {
            return 50.0; // Neutral if no category data
        }
        
        boolean hasMatch = worker.getCategories().stream()
            .anyMatch(cat -> cat.equalsIgnoreCase(post.getWorkerCategory()));
        
        return hasMatch ? 100.0 : 50.0;
    }
    
    /**
     * Get completion rate (0-100) based on hire history
     */
    private double getCompletionRate(WorkerProfile worker) {
        TrustScore trustScore = trustScoreRepository.findByWorker_WorkerId(worker.getWorkerId())
            .orElse(null);
        
        if (trustScore == null) {
            return 0.0;
        }
        
        return trustScore.getCompletionRate();
    }
    
    /**
     * Get top recommendations for a post
     */
    public List<Recommendation> getTopRecommendationsForPost(int postId) {
        List<Recommendation> recommendations = recommendationRepository.findByPostIdOrderByRank(postId);
        return recommendations.stream()
            .limit(MAX_RECOMMENDATIONS)
            .collect(Collectors.toList());
    }
    
    /**
     * DTO for worker scoring
     */
    public static class WorkerScoreDto {
        public WorkerProfile worker;
        public double score;
        public double skillMatchScore;
        public double distance;
        
        public WorkerScoreDto(WorkerProfile worker, double score, double skillMatchScore, double distance) {
            this.worker = worker;
            this.score = score;
            this.skillMatchScore = skillMatchScore;
            this.distance = distance;
        }
    }
}

