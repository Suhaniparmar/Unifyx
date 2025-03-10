package com.example.unifyx.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post {

    private int postId; // Firestore document ID (if needed)
    private OwnerProfile owner;

    private ContractorProfile contractor;

    @SerializedName("photos")
    private List<String> photo; // Changed to List<String>
    private String description;
    private String workerCategory;
    private String status;
    private String siteAddress;
    private String location;
    private String duration;

    // Constructor
    public Post(String description, String workerCategory, String siteAddress, String siteLocation, String duration, List<String> imageUrls) {
    }

    public Post(){

    }

    public Post(String description, String location) {
        this.description = description;
        this.location = location;
    }

    // Getters and Setters
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public OwnerProfile getOwner() {
        return owner;
    }

    public void setOwner(OwnerProfile owner) {
        this.owner = owner;
    }

    public ContractorProfile getContractor() {
        return contractor;
    }

    public void setContractor(ContractorProfile contractor) {
        this.contractor = contractor;
    }

    public List<String> getPhoto() {
        return photo;
    }

    public void setPhoto(List<String> photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkerCategory() {
        return workerCategory;
    }

    public void setWorkerCategory(String workerCategory) {
        this.workerCategory = workerCategory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
