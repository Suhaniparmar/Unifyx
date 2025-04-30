package org.example.unifyx.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "bid_raise")
public class BidRaise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bidRaiseId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String duration;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private WorkerProfile sender; // User who is making the bid

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // The post on which the bid is placed

    // Constructors
    public BidRaise() {}

    public BidRaise(Double amount, String duration, WorkerProfile sender, Post post) {
        this.amount = amount;
        this.duration = duration;
        this.sender = sender;
        this.post = post;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getBidRaiseId() {
        return bidRaiseId;
    }

    public void setBidRaiseId(Integer bidRaiseId) {
        this.bidRaiseId = bidRaiseId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public WorkerProfile getSender() {
        return sender;
    }

    public void setSender(WorkerProfile sender) {
        this.sender = sender;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }


}

