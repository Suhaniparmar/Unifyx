package org.example.unifyx.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hires")
public class Hire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hire_id")
    private int hireId;
    
    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", nullable = false)
    private Post post;
    
    @ManyToOne
    @JoinColumn(name = "worker_id", referencedColumnName = "worker_id", nullable = false)
    private WorkerProfile worker;
    
    @Column(name = "agreed_price", nullable = false)
    private Double agreedPrice;
    
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, COMPLETED, CANCELLED
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "owner_rating")
    private Double ownerRating; // 1-5 stars
    
    @Column(name = "owner_review", columnDefinition = "TEXT")
    private String ownerReview;
    
    // Constructors
    public Hire() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Hire(Post post, WorkerProfile worker, Double agreedPrice) {
        this.post = post;
        this.worker = worker;
        this.agreedPrice = agreedPrice;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters & Setters
    public int getHireId() { return hireId; }
    public void setHireId(int hireId) { this.hireId = hireId; }
    
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    
    public WorkerProfile getWorker() { return worker; }
    public void setWorker(WorkerProfile worker) { this.worker = worker; }
    
    public Double getAgreedPrice() { return agreedPrice; }
    public void setAgreedPrice(Double agreedPrice) { this.agreedPrice = agreedPrice; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Double getOwnerRating() { return ownerRating; }
    public void setOwnerRating(Double ownerRating) { this.ownerRating = ownerRating; }
    
    public String getOwnerReview() { return ownerReview; }
    public void setOwnerReview(String ownerReview) { this.ownerReview = ownerReview; }
}

