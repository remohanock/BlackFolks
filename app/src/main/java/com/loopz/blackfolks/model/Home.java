package com.loopz.blackfolks.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Home implements Serializable {
    String id;
    String name;
    String privilege;
    ArrayList<String> roomIds;

    public Home() {
    }

    public Home(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Home(String id, String name, String privilege, ArrayList<String> roomIds) {
        this.id = id;
        this.name = name;
        this.privilege = privilege;
        this.roomIds = roomIds;
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

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public List<String> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(ArrayList<String> roomIds) {
        this.roomIds = roomIds;
    }

    @Override
    public String toString() {
        return "Home{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", privilege='" + privilege + '\'' +
                ", roomIds=" + roomIds +
                '}';
    }
}
