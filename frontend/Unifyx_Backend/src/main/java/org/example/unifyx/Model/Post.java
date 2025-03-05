package org.example.unifyx.Model;
import jakarta.persistence.*;


@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private OwnerProfile owner;

    @ManyToOne
    @JoinColumn(name = "contractor_id")
    private ContractorProfile contractor;

    private String photo;
    private String description;
    private String workerCategory;
    private String status;
    private String siteAddress;
    private String location;
    private String duration;

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
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
// Getters and Setters
}

