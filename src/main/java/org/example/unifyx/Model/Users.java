package org.example.unifyx.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uid;

    @Column(nullable = false, unique = true)
    private String email;


    @Column(nullable = false)
    private String role;

    @Version  // Optimistic Locking
    private Long version;

    // Constructors
    public Users() {}

    public Users(String uid, String email, String role) {
        this.uid = uid;
        this.email = email;
        this.role = role;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() { return uid; }
    public void setUid(String id) { this.uid = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
