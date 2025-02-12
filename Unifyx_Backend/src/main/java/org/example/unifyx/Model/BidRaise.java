package org.example.unifyx.Model;

import jakarta.persistence.*;

@Entity
public class BidRaise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bidRaiseId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private int senderId;
    private String senderRole;
    private double amount;
    private String duration;
    private int receiverId;
    private String receiverRole;

    public int getBidRaiseId() {
        return bidRaiseId;
    }

    public void setBidRaiseId(int bidRaiseId) {
        this.bidRaiseId = bidRaiseId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

}

