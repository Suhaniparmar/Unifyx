package org.example.unifyx.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotes")
public class Quote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quote_id")
    private int quoteId;
    
    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", nullable = false)
    private Post post;
    
    @ManyToOne
    @JoinColumn(name = "worker_id", referencedColumnName = "worker_id")
    private WorkerProfile worker;

    @ManyToOne
    @JoinColumn(name = "contractor_id", referencedColumnName = "contractor_id")
    private ContractorProfile contractor;

    @Column(name = "sender_type", nullable = false)
    private String senderType = "worker";
    
    @Column(nullable = false)
    private Double price;
    
    @Column(name = "estimated_time")
    private String estimatedTime; // e.g., "2 days", "1 week"
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, ACCEPTED, REJECTED
    
    // Constructors
    public Quote() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Quote(Post post, WorkerProfile worker, Double price, String estimatedTime, String message) {
        this.post = post;
        this.worker = worker;
        this.senderType = "worker";
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Quote(Post post, ContractorProfile contractor, Double price, String estimatedTime, String message) {
        this.post = post;
        this.contractor = contractor;
        this.senderType = "contractor";
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    // Getters & Setters
    public int getQuoteId() { return quoteId; }
    public void setQuoteId(int quoteId) { this.quoteId = quoteId; }
    
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    
    public WorkerProfile getWorker() { return worker; }
    public void setWorker(WorkerProfile worker) { this.worker = worker; }

    public ContractorProfile getContractor() { return contractor; }
    public void setContractor(ContractorProfile contractor) { this.contractor = contractor; }

    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

