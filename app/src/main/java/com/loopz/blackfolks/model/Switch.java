package com.loopz.blackfolks.model;

import java.io.Serializable;

public class Switch implements Serializable {
    int id;
    String name;
    boolean isSwitchOn;

    public Switch() {
    }

    public Switch(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSwitchOn() {
        return isSwitchOn;
    }

    public void setSwitchOn(boolean switchOn) {
        this.isSwitchOn = switchOn;
    }

    @Override
    public String toString() {
        return "Switch{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isSwitchOn=" + isSwitchOn +
                '}';
    }
}
