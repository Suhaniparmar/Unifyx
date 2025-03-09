package org.example.unifyx.Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false)
    private Users user;

    private String description;
    private String workerCategory;
    private String siteAddress;
    private String location;
    private String duration;
    private String status;

    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    private List<String> photos;  // List of Cloudinary URLs

    // Getters & Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWorkerCategory() { return workerCategory; }
    public void setWorkerCategory(String workerCategory) { this.workerCategory = workerCategory; }

    public String getSiteAddress() { return siteAddress; }
    public void setSiteAddress(String siteAddress) { this.siteAddress = siteAddress; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }
}
