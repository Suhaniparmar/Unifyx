package com.example.unifyx.model;

import com.example.unifyx.model.Portfolio;



import java.util.List;


public class WorkerProfile {

    private int workerId;

    private String name;
    private String email;
    private String phoneNo;
    private String address;


    private List<Portfolio> portfolios;

    public WorkerProfile( String name, String email, String phoneNo, String address) {
        this.address = address;
        this.email = email;
        this.name = name;
        this.phoneNo = phoneNo;
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
}

