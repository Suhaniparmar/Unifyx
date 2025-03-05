package com.example.unifyx.model;


import java.util.List;

public class ContractorProfile {

    private int contractorId;

    private String name;
    private String email;
    private String siteAddress;
    private String phoneNo;


    private List<Portfolio> portfolios;

    private List<Post> posts;

    public ContractorProfile( String name, String email, String phoneNo, String siteAddress) {
        this.email = email;
        this.name = name;
        this.phoneNo = phoneNo;
        this.siteAddress = siteAddress;
    }

    // Getters and Setters
    public int getContractorId() { return contractorId; }
    public void setContractorId(int contractorId) { this.contractorId = contractorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSiteAddress() { return siteAddress; }
    public void setSiteAddress(String siteAddress) { this.siteAddress = siteAddress; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public List<Portfolio> getPortfolios() { return portfolios; }
    public void setPortfolios(List<Portfolio> portfolios) { this.portfolios = portfolios; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
}
