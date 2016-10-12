package com.webmne.salestracker.event.model;

/**
 * Created by vatsaldesai on 11-10-2016.
 */
public class EventModel {

    private String name;
    private String desc;
    private String createDate;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
