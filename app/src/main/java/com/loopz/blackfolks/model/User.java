package com.loopz.blackfolks.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class  User {

    String id;
    private String userId;
    private String displayName;

    public User() {
    }

    public User(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
