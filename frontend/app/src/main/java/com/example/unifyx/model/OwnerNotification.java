package com.example.unifyx.model;

import com.google.gson.annotations.SerializedName;

public class OwnerNotification {

    @SerializedName("notificationId")
    private int notificationId;

    @SerializedName("ownerUid")
    private String ownerUid;

    @SerializedName("postId")
    private int postId;

    @SerializedName("quoteId")
    private int quoteId;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("read")
    private boolean read;

    @SerializedName("createdAt")
    private String createdAt;

    public int getNotificationId() { return notificationId; }
    public int getPostId() { return postId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public String getCreatedAt() { return createdAt; }
}

