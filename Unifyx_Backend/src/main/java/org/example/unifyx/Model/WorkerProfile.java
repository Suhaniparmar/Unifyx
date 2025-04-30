package org.example.unifyx.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "worker_profile")
public class WorkerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "worker_id")
    private int workerId;

    private String name;
    private String email;
    private String phoneNo;
    private String address;

    @ElementCollection
    @CollectionTable(name = "worker_categories", joinColumns = @JoinColumn(name = "worker_id"))
    @Column(name = "category")
    private List<String> categories;// Multiple categories a worker can choose


    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Portfolio> portfolios;

    public WorkerProfile() {
        // Required by JPA / Jackson
    }


    // Getters and Setters

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
}

