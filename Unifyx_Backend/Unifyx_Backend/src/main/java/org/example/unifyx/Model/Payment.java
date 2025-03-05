package org.example.unifyx.Model;


import jakarta.persistence.*;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private int payerId;
    private String payerRole;
    private int payeeId;
    private String payeeRole;
    private double amount;
    private String status;

    // Getters and Setters

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public int getPayerId() {
        return payerId;
    }

    public void setPayerId(int payerId) {
        this.payerId = payerId;
    }

    public String getPayerRole() {
        return payerRole;
    }

    public void setPayerRole(String payerRole) {
        this.payerRole = payerRole;
    }

    public int getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(int payeeId) {
        this.payeeId = payeeId;
    }

    public String getPayeeRole() {
        return payeeRole;
    }

    public void setPayeeRole(String payeeRole) {
        this.payeeRole = payeeRole;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
