package com.example.unifyx.model;

import com.google.gson.annotations.SerializedName;

public class Quote {
    @SerializedName("quoteId")
    private int quoteId;
    
    @SerializedName("postId")
    private int postId;
    
    @SerializedName("workerId")
    private int workerId;
    
    @SerializedName("price")
    private Double price;
    
    @SerializedName("estimatedTime")
    private String estimatedTime;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("status")
    private String status;

    @SerializedName("post")
    private Post post;
    
    @SerializedName("worker")
    private WorkerProfile worker;

    @SerializedName("contractor")
    private ContractorProfile contractor;

    @SerializedName("senderType")
    private String senderType;
    
    // Constructor
    public Quote() {}
    
    public Quote(int postId, int workerId, Double price, String estimatedTime, String message) {
        this.postId = postId;
        this.workerId = workerId;
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.message = message;
    }
    
    // Getters & Setters
    public int getQuoteId() { return quoteId; }
    public void setQuoteId(int quoteId) { this.quoteId = quoteId; }
    
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    
    public int getWorkerId() { return workerId; }
    public void setWorkerId(int workerId) { this.workerId = workerId; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public WorkerProfile getWorker() { return worker; }
    public void setWorker(WorkerProfile worker) { this.worker = worker; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public ContractorProfile getContractor() { return contractor; }
    public void setContractor(ContractorProfile contractor) { this.contractor = contractor; }

    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }
}

