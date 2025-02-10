package org.example.unifyx.Entities;

import jakarta.persistence.*;

@Entity
public class BidReceive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bidId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private int receiverId;
    private String receiverRole;
    private int senderId;
    private String senderRole;
    private String workerPortfolio;
    private double proposedAmt;
    private String proposedDuration;

    // Getters and Setters

    public int getBidId() {
        return bidId;
    }

    public void setBidId(int bidId) {
        this.bidId = bidId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverRole() {
        return receiverRole;
    }

    public void setReceiverRole(String receiverRole) {
        this.receiverRole = receiverRole;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getWorkerPortfolio() {
        return workerPortfolio;
    }

    public void setWorkerPortfolio(String workerPortfolio) {
        this.workerPortfolio = workerPortfolio;
    }

    public double getProposedAmt() {
        return proposedAmt;
    }

    public void setProposedAmt(double proposedAmt) {
        this.proposedAmt = proposedAmt;
    }

    public String getProposedDuration() {
        return proposedDuration;
    }

    public void setProposedDuration(String proposedDuration) {
        this.proposedDuration = proposedDuration;
    }
}

