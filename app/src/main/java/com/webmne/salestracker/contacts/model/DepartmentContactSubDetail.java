package com.webmne.salestracker.contacts.model;

/**
 * Created by vatsaldesai on 02-09-2016.
 */
public class DepartmentContactSubDetail {

    String dept_id,name,email,phone;

    public DepartmentContactSubDetail(String dept_id, String name, String email, String phone) {
        this.dept_id = dept_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getDept_id() {
        return dept_id;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
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
