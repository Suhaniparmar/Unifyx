package org.example.unifyx.Model;

import jakarta.persistence.*;
@Entity
@Table(name = "bid_raise")
public class BidRaise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_raise_id") // Ensure it matches DB column name
    private int bidRaiseId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "sender_id")
    private int senderId;

    @Column(name = "sender_role")
    private String senderRole;

    private double amount;
    private String duration;

    @Column(name = "receiver_id")
    private int receiverId;

    @Column(name = "receiver_role")
    private String receiverRole;

    // Getters and Setters
    public int getBidRaiseId() { return bidRaiseId; }
    public void setBidRaiseId(int bidRaiseId) { this.bidRaiseId = bidRaiseId; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getReceiverRole() { return receiverRole; }
    public void setReceiverRole(String receiverRole) { this.receiverRole = receiverRole; }
}
