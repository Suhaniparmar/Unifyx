package com.example.unifyx.model;

public class BidRaise {
    private String uid;
    private int senderId;
    private int postId;
    private double amount;
    private String duration;
    private WorkerProfile sender;

    public BidRaise(int senderId, int postId, double amount, String duration) {
        this.senderId = senderId;
        this.postId = postId;
        this.amount = amount;
        this.duration = duration;
    }

    public WorkerProfile getSender() {
        return sender;
    }

    public void setSender(WorkerProfile sender) {
        this.sender = sender;
    }

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
}
