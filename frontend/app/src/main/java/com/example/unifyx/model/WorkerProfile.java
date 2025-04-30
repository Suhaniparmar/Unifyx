package com.example.unifyx.model;

import com.example.unifyx.owner.Profile;
import com.google.gson.annotations.SerializedName;


import java.util.List;
public class WorkerProfile implements Profile {

    private int workerId;
    private String name;
    private String email;
    private String phoneNo;
    private String address;

    private List<String> categories;


    public WorkerProfile(String name, String email, String phoneNo, String address, List<String> categories) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.address = address;
        this.categories = categories;
    }

    @Override
    public String getName() { return name; }
    @Override
    public String getEmail() { return email; }
    @Override
    public String getPhoneNo() { return phoneNo; }
    @Override
    public String getAddress() { return address; }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories =categories;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

//    public List<Portfolio> getPortfolios() {
//        return portfolios;
//    }
//
//    public void setPortfolios(List<Portfolio> portfolios) {
//        this.portfolios = portfolios;
//    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    @Override
    public String toString() {
        return "WorkerProfile{" +
                "address='" + address + '\'' +
                ", workerId=" + workerId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", categories=" + categories +
                '}';
    }
}




