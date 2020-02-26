package com.loopz.blackfolks.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SceneMode implements Serializable {
    String id;
    String name;
    String homeId;
    String userId;
    boolean isOn;
    ArrayList<String> roomSwitch;

    public SceneMode() {
    }

    public SceneMode(String name, String homeId, String userId, ArrayList<String> roomSwitch) {
        this.name = name;
        this.homeId = homeId;
        this.userId = userId;
        this.roomSwitch = roomSwitch;
    }

    public SceneMode(String id,String name, String homeId, String userId, ArrayList<String> roomSwitch) {
        this.id = id;
        this.name = name;
        this.homeId = homeId;
        this.userId = userId;
        this.roomSwitch = roomSwitch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getRoomSwitch() {
        return roomSwitch;
    }

    public void setRoomSwitch(ArrayList<String> roomSwitch) {
        this.roomSwitch = roomSwitch;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SceneMode{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", homeId='" + homeId + '\'' +
                ", userId='" + userId + '\'' +
                ", isOn=" + isOn +
                ", roomSwitch=" + roomSwitch +
                '}';
    }
}
