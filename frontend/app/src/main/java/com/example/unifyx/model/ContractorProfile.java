package com.example.unifyx.model;


import com.example.unifyx.owner.Profile;

import java.util.List;

public class ContractorProfile implements Profile {

    private int contractorId;

    private String name;
    private String email;
    private String address;
    private String phoneNo;


    private List<Portfolio> portfolios;

    private List<Post> posts;
    private List<String> contractorCategories;

    public ContractorProfile(String name, String email, String phoneNo, String address, List<String> workerCategories) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.address = address;
        this.contractorCategories = workerCategories;
    }

//    public ContractorProfile(String name, String email, String phoneNo, String siteAddress) {
//        this.email = email;
//        this.name = name;
//        this.phoneNo = phoneNo;
//        this.address = siteAddress;
//    }

    // Getters and Setters
    public int getContractorId() { return contractorId; }
    public void setContractorId(int contractorId) { this.contractorId = contractorId; }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public List<Portfolio> getPortfolios() { return portfolios; }
    public void setPortfolios(List<Portfolio> portfolios) { this.portfolios = portfolios; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }

    public List<String> getContractorCategories() {
        return contractorCategories;
    }

    public void setContractorCategories(List<String> contractorCategories) {
        this.contractorCategories = contractorCategories;
    }
}
