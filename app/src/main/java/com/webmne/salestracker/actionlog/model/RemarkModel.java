package com.webmne.salestracker.actionlog.model;

/**
 * Created by vatsaldesai on 13-09-2016.
 */
public class RemarkModel {

    String name;
    String detail;
    String date;
    String position;


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
