package org.example.unifyx.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "trust_scores")
public class TrustScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private int scoreId;

    @OneToOne
    @JoinColumn(name = "worker_id", referencedColumnName = "worker_id", nullable = false)
    @JsonIgnore
    private WorkerProfile worker;
    
    @Column(nullable = false)
    private Double score = 5.0; // Base score 5.0/10
    
    @Column(name = "rating_avg")
    private Double ratingAvg = 0.0; // Average of owner ratings
    
    @Column(name = "completion_rate")
    private Double completionRate = 0.0; // Percentage of completed jobs
    
    @Column(name = "repeat_clients_count")
    private int repeatClientsCount = 0;
    
    @Column(name = "total_jobs_completed")
    private int totalJobsCompleted = 0;
    
    // Constructors
    public TrustScore() {
        this.score = 5.0;
    }
    
    public TrustScore(WorkerProfile worker) {
        this.worker = worker;
        this.score = 5.0;
        this.ratingAvg = 0.0;
        this.completionRate = 0.0;
    }
    
    // Calculate overall trust score based on factors
    public Double calculateTrustScore() {
        // Formula: 40% rating + 35% completion rate + 15% repeat clients + 10% baseline
        Double weighted = (ratingAvg * 0.4) + (completionRate * 0.35) + (Math.min(repeatClientsCount, 10) * 0.5); // 0.5 per repeat client, capped at 10
        return Math.min(Math.max(weighted, 0.0), 10.0); // Clamp between 0 and 10
    }
    
    // Getters & Setters
    public int getScoreId() { return scoreId; }
    public void setScoreId(int scoreId) { this.scoreId = scoreId; }
    
    public WorkerProfile getWorker() { return worker; }
    public void setWorker(WorkerProfile worker) { this.worker = worker; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = Math.min(Math.max(score, 0.0), 10.0); }
    
    public Double getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(Double ratingAvg) { this.ratingAvg = Math.min(Math.max(ratingAvg, 0.0), 10.0); }
    
    public Double getCompletionRate() { return completionRate; }
    public void setCompletionRate(Double completionRate) { this.completionRate = Math.min(Math.max(completionRate, 0.0), 100.0); }
    
    public int getRepeatClientsCount() { return repeatClientsCount; }
    public void setRepeatClientsCount(int repeatClientsCount) { this.repeatClientsCount = repeatClientsCount; }
    
    public int getTotalJobsCompleted() { return totalJobsCompleted; }
    public void setTotalJobsCompleted(int totalJobsCompleted) { this.totalJobsCompleted = totalJobsCompleted; }
}

