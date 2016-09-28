package com.webmne.salestracker.employee.model;

/**
 * Created by vatsaldesai on 26-09-2016.
 */

public class PositionModel {

    String id, name;

    public PositionModel(String id, String name) {
        this.id = id;
        this.name = name;
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
}
