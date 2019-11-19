package com.loopz.blackfolks.model;

import java.io.Serializable;

public class Room implements Serializable {
    String id;
    String name;
    String buttonState;

    public Room() {
    }

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.buttonState="0000";
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

    public String getButtonState() {
        return buttonState;
    }

    public void setButtonState(String buttonState) {
        this.buttonState = buttonState;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", buttonState='" + buttonState + '\'' +
                '}';
    }
}
