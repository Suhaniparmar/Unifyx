package org.example.unifyx.Model;


import jakarta.persistence.*;
import lombok.ToString;

@ToString
@Entity
public class Negotiate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int negotiateId;

    @ManyToOne
    @JoinColumn(name = "bid_id")
    private BidReceive bid;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private int negotiatorId;
    private String negotiatorRole;
    private double estimation;
    private String status;

    // Getters and Setters

    public int getNegotiateId() {
        return negotiateId;
    }

    public void setNegotiateId(int negotiateId) {
        this.negotiateId = negotiateId;
    }

    public BidReceive getBid() {
        return bid;
    }

    public void setBid(BidReceive bid) {
        this.bid = bid;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public int getNegotiatorId() {
        return negotiatorId;
    }

    public void setNegotiatorId(int negotiatorId) {
        this.negotiatorId = negotiatorId;
    }

    public String getNegotiatorRole() {
        return negotiatorRole;
    }

    public void setNegotiatorRole(String negotiatorRole) {
        this.negotiatorRole = negotiatorRole;
    }

    public double getEstimation() {
        return estimation;
    }

    public void setEstimation(double estimation) {
        this.estimation = estimation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

