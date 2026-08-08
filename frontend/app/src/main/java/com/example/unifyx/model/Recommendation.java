package com.example.unifyx.model;

import com.google.gson.annotations.SerializedName;

public class Recommendation {
    @SerializedName("recommendationId")
    private int recommendationId;
    
    @SerializedName("postId")
    private int postId;
    
    @SerializedName("workerId")
    private int workerId;
    
    @SerializedName("score")
    private Double score;
    
    @SerializedName("skillMatchScore")
    private Double skillMatchScore;
    
    @SerializedName("distanceKm")
    private Double distanceKm;
    
    @SerializedName("rank")
    private int rank;
    
    @SerializedName("isContacted")
    private Boolean isContacted;
    
    @SerializedName("worker")
    private WorkerProfile worker;
    
    // Constructor
    public Recommendation() {}
    
    // Getters & Setters
    public int getRecommendationId() { return recommendationId; }
    public void setRecommendationId(int recommendationId) { this.recommendationId = recommendationId; }
    
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    
    public int getWorkerId() { return workerId; }
    public void setWorkerId(int workerId) { this.workerId = workerId; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    
    public Double getSkillMatchScore() { return skillMatchScore; }
    public void setSkillMatchScore(Double skillMatchScore) { this.skillMatchScore = skillMatchScore; }
    
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    
    public Boolean getIsContacted() { return isContacted; }
    public void setIsContacted(Boolean isContacted) { this.isContacted = isContacted; }
    
    public WorkerProfile getWorker() { return worker; }
    public void setWorker(WorkerProfile worker) { this.worker = worker; }
}

