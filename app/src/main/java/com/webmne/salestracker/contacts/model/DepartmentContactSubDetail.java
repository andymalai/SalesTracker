package com.webmne.salestracker.contacts.model;

/**
 * Created by vatsaldesai on 02-09-2016.
 */
public class DepartmentContactSubDetail {

    String name,email,phone;

    public DepartmentContactSubDetail(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
