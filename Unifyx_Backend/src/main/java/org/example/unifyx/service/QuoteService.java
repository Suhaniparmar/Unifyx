package org.example.unifyx.service;

import org.example.unifyx.Model.*;
import org.example.unifyx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing quotes submitted by workers on job posts
 */
@Service
@Transactional
public class QuoteService {
    
    @Autowired
    private QuoteRepository quoteRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    @Autowired
    private ContractorProfileRepository contractorProfileRepository;
    
    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private OwnerNotificationService ownerNotificationService;
    
    /**
     * Create a new quote from a worker for a job post
     */
    public Quote submitQuote(int postId, Integer workerId, Integer contractorId, Double price, String estimatedTime, String message) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        if ((workerId == null && contractorId == null) || (workerId != null && contractorId != null)) {
            throw new IllegalArgumentException("Provide exactly one sender: workerId or contractorId");
        }

        if (workerId != null) {
            WorkerProfile worker = workerProfileRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found: " + workerId));

            Optional<Quote> existingQuote = quoteRepository.findByPostIdAndWorkerId(postId, workerId);
            if (existingQuote.isPresent()) {
                Quote quote = existingQuote.get();
                quote.setWorker(worker);
                quote.setContractor(null);
                quote.setSenderType("worker");
                quote.setPrice(price);
                quote.setEstimatedTime(estimatedTime);
                quote.setMessage(message);
                Quote savedQuote = quoteRepository.save(quote);
                notifyOwner(post, savedQuote, worker.getName(), true);
                return savedQuote;
            }

            Quote quote = new Quote(post, worker, price, estimatedTime, message);
            Quote savedQuote = quoteRepository.save(quote);
            markRecommendationAsContacted(postId, workerId);
            notifyOwner(post, savedQuote, worker.getName(), false);
            return savedQuote;
        }

        ContractorProfile contractor = contractorProfileRepository.findById(contractorId)
            .orElseThrow(() -> new IllegalArgumentException("Contractor not found: " + contractorId));

        Optional<Quote> existingQuote = quoteRepository.findByPostIdAndContractorId(postId, contractorId);
        if (existingQuote.isPresent()) {
            Quote quote = existingQuote.get();
            quote.setContractor(contractor);
            quote.setWorker(null);
            quote.setSenderType("contractor");
            quote.setPrice(price);
            quote.setEstimatedTime(estimatedTime);
            quote.setMessage(message);
            Quote savedQuote = quoteRepository.save(quote);
            notifyOwner(post, savedQuote, contractor.getName(), true);
            return savedQuote;
        }

        Quote quote = new Quote(post, contractor, price, estimatedTime, message);
        Quote savedQuote = quoteRepository.save(quote);
        notifyOwner(post, savedQuote, contractor.getName(), false);
        return savedQuote;
    }

    private void notifyOwner(Post post, Quote quote, String senderName, boolean isUpdate) {
        if (post.getUser() != null && post.getUser().getUid() != null) {
            ownerNotificationService.createQuoteNotification(
                post.getUser().getUid(),
                post.getPostId(),
                quote.getQuoteId(),
                senderName == null || senderName.isBlank() ? "A provider" : senderName,
                isUpdate
            );
        }
    }
    
    /**
     * Get all quotes for a specific post, sorted by trust score and price
     */
    public List<Quote> getQuotesForPost(int postId) {
        return quoteRepository.findQuotesForPostSortedByTrustAndPrice(postId);
    }
    
    /**
     * Get a specific quote by ID
     */
    public Quote getQuoteById(int quoteId) {
        return quoteRepository.findById(quoteId)
            .orElseThrow(() -> new IllegalArgumentException("Quote not found: " + quoteId));
    }
    
    /**
     * Accept a quote (owner selects a worker)
     */
    public Quote acceptQuote(int quoteId) {
        Quote quote = getQuoteById(quoteId);
        quote.setStatus("ACCEPTED");
        return quoteRepository.save(quote);
    }
    
    /**
     * Reject a quote
     */
    public Quote rejectQuote(int quoteId) {
        Quote quote = getQuoteById(quoteId);
        quote.setStatus("REJECTED");
        return quoteRepository.save(quote);
    }
    
    /**
     * Get all quotes from a specific worker
     */
    public List<Quote> getQuotesFromWorker(int workerId) {
        return quoteRepository.findByWorker_WorkerId(workerId);
    }

    public List<Quote> getAllQuotesForWorker(int workerId) {
        return quoteRepository.findByWorker_WorkerIdOrderByCreatedAtDesc(workerId);
    }

    public List<Quote> getAllQuotesForContractor(int contractorId) {
        return quoteRepository.findByContractor_ContractorIdOrderByCreatedAtDesc(contractorId);
    }
    
    /**
     * Get pending quotes for a worker
     */
    public List<Quote> getPendingQuotesForWorker(int workerId) {
        return quoteRepository.findByStatusAndWorker_WorkerId("PENDING", workerId);
    }
    
    /**
     * Mark a recommendation as contacted when worker submits a quote
     */
    private void markRecommendationAsContacted(int postId, int workerId) {
        Optional<Recommendation> recommendation = recommendationRepository.findByPost_PostIdAndWorker_WorkerId(postId, workerId);
        if (recommendation.isPresent()) {
            Recommendation rec = recommendation.get();
            rec.setIsContacted(true);
            recommendationRepository.save(rec);
        }
    }

    public void deleteQuotesForPost(int postId) {
        quoteRepository.deleteByPost_PostId(postId);
    }
}

