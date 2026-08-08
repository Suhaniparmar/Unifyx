package org.example.unifyx.controller;

import org.example.unifyx.Model.Quote;
import org.example.unifyx.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing worker quotes on job posts
 */
@RestController
@RequestMapping("/quotes")
public class QuoteController {
    
    @Autowired
    private QuoteService quoteService;
    
    /**
     * Submit a new quote from a worker
     * POST /quotes
     * Body: {
     *   "postId": int,
     *   "workerId": int,
     *   "price": double,
     *   "estimatedTime": "string",
     *   "message": "string"
     * }
     */
    @PostMapping
    public ResponseEntity<?> submitQuote(@RequestBody Map<String, Object> request) {
        try {
            int postId = ((Number) request.get("postId")).intValue();
            Integer workerId = request.get("workerId") == null ? null : ((Number) request.get("workerId")).intValue();
            Integer contractorId = request.get("contractorId") == null ? null : ((Number) request.get("contractorId")).intValue();
            Double price = ((Number) request.get("price")).doubleValue();
            String estimatedTime = (String) request.get("estimatedTime");
            String message = (String) request.get("message");
            
            Quote quote = quoteService.submitQuote(postId, workerId, contractorId, price, estimatedTime, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(quote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to submit quote"));
        }
    }
    
    /**
     * Get all quotes for a specific post
     * GET /quotes/post/{postId}
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getQuotesForPost(@PathVariable int postId) {
        try {
            List<Quote> quotes = quoteService.getQuotesForPost(postId);
            return ResponseEntity.ok(quotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch quotes"));
        }
    }
    
    /**
     * Get a specific quote by ID
     * GET /quotes/{quoteId}
     */
    @GetMapping("/{quoteId}")
    public ResponseEntity<?> getQuoteById(@PathVariable int quoteId) {
        try {
            Quote quote = quoteService.getQuoteById(quoteId);
            return ResponseEntity.ok(quote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Accept a quote (owner selects worker)
     * PUT /quotes/{quoteId}/accept
     */
    @PutMapping("/{quoteId}/accept")
    public ResponseEntity<?> acceptQuote(@PathVariable int quoteId) {
        try {
            Quote quote = quoteService.acceptQuote(quoteId);
            return ResponseEntity.ok(quote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Reject a quote
     * PUT /quotes/{quoteId}/reject
     */
    @PutMapping("/{quoteId}/reject")
    public ResponseEntity<?> rejectQuote(@PathVariable int quoteId) {
        try {
            Quote quote = quoteService.rejectQuote(quoteId);
            return ResponseEntity.ok(quote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all quotes from a specific worker
     * GET /quotes/worker/{workerId}
     */
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<?> getQuotesFromWorker(@PathVariable int workerId) {
        try {
            List<Quote> quotes = quoteService.getQuotesFromWorker(workerId);
            return ResponseEntity.ok(quotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch worker quotes"));
        }
    }
    
    /**
     * Get pending quotes for a worker
     * GET /quotes/worker/{workerId}/pending
     */
    @GetMapping("/worker/{workerId}/pending")
    public ResponseEntity<?> getPendingQuotesForWorker(@PathVariable int workerId) {
        try {
            List<Quote> quotes = quoteService.getPendingQuotesForWorker(workerId);
            return ResponseEntity.ok(quotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch pending quotes"));
        }
    }

    /**
     * Get all quotes from a worker (history/status screen)
     * GET /quotes/worker/{workerId}/all
     */
    @GetMapping("/worker/{workerId}/all")
    public ResponseEntity<?> getAllQuotesForWorker(@PathVariable int workerId) {
        try {
            return ResponseEntity.ok(quoteService.getAllQuotesForWorker(workerId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch worker quote history"));
        }
    }

    /**
     * Get all quotes from a contractor (history/status screen)
     * GET /quotes/contractor/{contractorId}/all
     */
    @GetMapping("/contractor/{contractorId}/all")
    public ResponseEntity<?> getAllQuotesForContractor(@PathVariable int contractorId) {
        try {
            return ResponseEntity.ok(quoteService.getAllQuotesForContractor(contractorId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch contractor quote history"));
        }
    }
}

