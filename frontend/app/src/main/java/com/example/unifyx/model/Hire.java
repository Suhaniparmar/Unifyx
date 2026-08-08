package com.example.unifyx.model;

import com.google.gson.annotations.SerializedName;

public class Hire {
    @SerializedName("hireId")
    private int hireId;
    
    @SerializedName("postId")
    private int postId;
    
    @SerializedName("workerId")
    private int workerId;
    
    @SerializedName("agreedPrice")
    private Double agreedPrice;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("completedAt")
    private String completedAt;
    
    @SerializedName("ownerRating")
    private Double ownerRating;
    
    @SerializedName("ownerReview")
    private String ownerReview;
    
    @SerializedName("worker")
    private WorkerProfile worker;
    
    // Constructor
    public Hire() {}
    
    public Hire(int postId, int workerId, Double agreedPrice) {
        this.postId = postId;
        this.workerId = workerId;
        this.agreedPrice = agreedPrice;
    }
    
    // Getters & Setters
    public int getHireId() { return hireId; }
    public void setHireId(int hireId) { this.hireId = hireId; }
    
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    
    public int getWorkerId() { return workerId; }
    public void setWorkerId(int workerId) { this.workerId = workerId; }
    
    public Double getAgreedPrice() { return agreedPrice; }
    public void setAgreedPrice(Double agreedPrice) { this.agreedPrice = agreedPrice; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    
    public Double getOwnerRating() { return ownerRating; }
    public void setOwnerRating(Double ownerRating) { this.ownerRating = ownerRating; }
    
    public String getOwnerReview() { return ownerReview; }
    public void setOwnerReview(String ownerReview) { this.ownerReview = ownerReview; }
    
    public WorkerProfile getWorker() { return worker; }
    public void setWorker(WorkerProfile worker) { this.worker = worker; }
}

