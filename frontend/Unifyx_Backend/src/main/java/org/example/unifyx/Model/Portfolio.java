package org.example.unifyx.Model;

import jakarta.persistence.*;

@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int portfolioId;

    @ManyToOne
    @JoinColumn(name = "contractor_id", nullable = false)
    private ContractorProfile contractor;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private WorkerProfile worker;

    private String skills;
    private int pastExperience;

    @Lob
    private String pastWorkPhotos;  // Store as large text (URLs or base64)

    private String pastSiteDetails;
    private String review;

    // Getters and Setters
    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public ContractorProfile getContractor() {
        return contractor;
    }

    public void setContractor(ContractorProfile contractor) {
        this.contractor = contractor;
    }

    public WorkerProfile getWorker() {
        return worker;
    }

    public void setWorker(WorkerProfile worker) {
        this.worker = worker;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public int getPastExperience() {
        return pastExperience;
    }

    public void setPastExperience(int pastExperience) {
        this.pastExperience = pastExperience;
    }

    public String getPastWorkPhotos() {
        return pastWorkPhotos;
    }

    public void setPastWorkPhotos(String pastWorkPhotos) {
        this.pastWorkPhotos = pastWorkPhotos;
    }

    public String getPastSiteDetails() {
        return pastSiteDetails;
    }

    public void setPastSiteDetails(String pastSiteDetails) {
        this.pastSiteDetails = pastSiteDetails;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
