package com.loopz.blackfolks.model;

import java.util.ArrayList;

public class UserHome {
    String homeId;
    String userId;
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
}
