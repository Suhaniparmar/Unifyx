package org.example.unifyx.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "recommendations")
public class Recommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private int recommendationId;
    
    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", nullable = false)
    private Post post;
    
    @ManyToOne
    @JoinColumn(name = "worker_id", referencedColumnName = "worker_id", nullable = false)
    private WorkerProfile worker;
    
    @Column(nullable = false)
    private Double score; // Calculated recommendation score (0-100)
    
    @Column(name = "skill_match_score")
    private Double skillMatchScore; // How well worker skills match job (0-100)
    
    @Column(name = "distance_km")
    private Double distanceKm; // Distance from job location
    
    @Column(name = "`rank`")
    private int rank; // Ranking position (1-5)
    
    @Column(name = "is_contacted")
    private Boolean isContacted = false; // Has owner reached out or has worker quoted
    
    // Constructors
    public Recommendation() {}
    
    public Recommendation(Post post, WorkerProfile worker, Double score, Double skillMatch, Double distance, int rank) {
        this.post = post;
        this.worker = worker;
        this.score = score;
        this.skillMatchScore = skillMatch;
        this.distanceKm = distance;
        this.rank = rank;
        this.isContacted = false;
    }
    
    // Getters & Setters
    public int getRecommendationId() { return recommendationId; }
    public void setRecommendationId(int recommendationId) { this.recommendationId = recommendationId; }
    
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    
    public WorkerProfile getWorker() { return worker; }
    public void setWorker(WorkerProfile worker) { this.worker = worker; }
    
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
}


