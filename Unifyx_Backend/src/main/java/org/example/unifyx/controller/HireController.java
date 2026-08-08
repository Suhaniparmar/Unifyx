package org.example.unifyx.controller;

import org.example.unifyx.Model.Hire;
import org.example.unifyx.service.HireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing hire agreements between owners and workers
 */
@RestController
@RequestMapping("/hires")
public class HireController {
    
    @Autowired
    private HireService hireService;
    
    /**
     * Create a hire agreement (owner selects a worker)
     * POST /hires
     * Body: {
     *   "postId": int,
     *   "workerId": int,
     *   "agreedPrice": double
     * }
     */
    @PostMapping
    public ResponseEntity<?> createHire(@RequestBody Map<String, Object> request) {
        try {
            int postId = ((Number) request.get("postId")).intValue();
            int workerId = ((Number) request.get("workerId")).intValue();
            Double agreedPrice = ((Number) request.get("agreedPrice")).doubleValue();
            
            Hire hire = hireService.createHire(postId, workerId, agreedPrice);
            return ResponseEntity.status(HttpStatus.CREATED).body(hire);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create hire agreement"));
        }
    }
    
    /**
     * Get a specific hire by ID
     * GET /hires/{hireId}
     */
    @GetMapping("/{hireId}")
    public ResponseEntity<?> getHireById(@PathVariable int hireId) {
        try {
            Hire hire = hireService.getHireById(hireId);
            return ResponseEntity.ok(hire);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get active hires for a post
     * GET /hires/post/{postId}/active
     */
    @GetMapping("/post/{postId}/active")
    public ResponseEntity<?> getActiveHiresForPost(@PathVariable int postId) {
        try {
            List<Hire> hires = hireService.getActiveHiresForPost(postId);
            return ResponseEntity.ok(hires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch hires"));
        }
    }
    
    /**
     * Get all hires for a worker
     * GET /hires/worker/{workerId}
     */
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<?> getHiresForWorker(@PathVariable int workerId) {
        try {
            List<Hire> hires = hireService.getHiresForWorker(workerId);
            return ResponseEntity.ok(hires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch worker hires"));
        }
    }
    
    /**
     * Mark a hire as completed
     * PUT /hires/{hireId}/complete
     */
    @PutMapping("/{hireId}/complete")
    public ResponseEntity<?> completeHire(@PathVariable int hireId) {
        try {
            Hire hire = hireService.completeHire(hireId);
            return ResponseEntity.ok(hire);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Rate and review a completed hire
     * PUT /hires/{hireId}/rate
     * Body: {
     *   "rating": double (1-5),
     *   "review": "string"
     * }
     */
    @PutMapping("/{hireId}/rate")
    public ResponseEntity<?> rateHire(@PathVariable int hireId, @RequestBody Map<String, Object> request) {
        try {
            Double rating = ((Number) request.get("rating")).doubleValue();
            String review = (String) request.get("review");
            
            Hire hire = hireService.rateHire(hireId, rating, review);
            return ResponseEntity.ok(hire);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to rate hire"));
        }
    }
    
    /**
     * Cancel a hire agreement
     * PUT /hires/{hireId}/cancel
     */
    @PutMapping("/{hireId}/cancel")
    public ResponseEntity<?> cancelHire(@PathVariable int hireId) {
        try {
            Hire hire = hireService.cancelHire(hireId);
            return ResponseEntity.ok(hire);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }
}

