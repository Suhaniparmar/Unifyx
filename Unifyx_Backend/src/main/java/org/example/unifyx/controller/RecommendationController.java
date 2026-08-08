package org.example.unifyx.controller;

import org.example.unifyx.Model.Recommendation;
import org.example.unifyx.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for getting worker recommendations for job posts
 */
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    /**
     * Get top recommendations for a post
     * GET /recommendations/post/{postId}
     * Returns top 5 recommended workers ranked by matching algorithm
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getRecommendationsForPost(@PathVariable int postId) {
        try {
            List<Recommendation> recommendations = recommendationService.getTopRecommendationsForPost(postId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch recommendations"));
        }
    }
    
    /**
     * Manually trigger recommendation generation for a post
     * POST /recommendations/post/{postId}/generate
     * (Usually auto-triggered on post creation, but available for manual refresh)
     */
    @PostMapping("/post/{postId}/generate")
    public ResponseEntity<?> generateRecommendations(@PathVariable int postId) {
        try {
            List<Recommendation> recommendations = recommendationService.generateRecommendationsForPost(postId);
            return ResponseEntity.status(HttpStatus.CREATED).body(recommendations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to generate recommendations"));
        }
    }
}

