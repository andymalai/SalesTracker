package com.webmne.salestracker.contacts.model;

/**
 * Created by vatsaldesai on 22-08-2016.
 */
public class DepartmentContactSubModel {

    private String name,mobile,email;

    public DepartmentContactSubModel(String name, String mobile, String email) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
