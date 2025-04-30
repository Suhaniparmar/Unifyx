package com.example.unifyx.model;

import com.google.gson.annotations.SerializedName;

public class Users {

    @SerializedName("id")
    private Long id;

    @SerializedName("uid")
    private String uid;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("version")
    private Long version;



    // Constructors
    public Users() {}

    public Users(String uid, String email, String role) {
        this.uid=uid;
        this.email = email;
        this.role = role;
    }

    // Getters & Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
