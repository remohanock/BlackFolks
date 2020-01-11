package com.loopz.blackfolks.model;

import java.io.Serializable;
import java.util.ArrayList;

public class UserHome implements Serializable {
    String id;
    String homeId;
    String userId;
    User user;
    String priority;
    ArrayList<String> roomIds;

    public UserHome() {
    }

    public UserHome(String homeId, String userId, String priroity, ArrayList<String> roomIds) {
        this.homeId = homeId;
        this.userId = userId;
        this.priority = priroity;
        this.roomIds = roomIds;
    }

    public UserHome(String id,String homeId, String userId, String priroity, ArrayList<String> roomIds) {
        this.id = id;
        this.homeId = homeId;
        this.userId = userId;
        this.priority = priroity;
        this.roomIds = roomIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public ArrayList<String> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(ArrayList<String> roomIds) {
        this.roomIds = roomIds;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserHome{" +
                "id='" + id + '\'' +
                ", homeId='" + homeId + '\'' +
                ", userId='" + userId + '\'' +
                ", user=" + user +
                ", priority='" + priority + '\'' +
                ", roomIds=" + roomIds +
                '}';
    }
}
