package com.webmne.salestracker.employee.model;

/**
 * Created by vatsaldesai on 23-09-2016.
 */

public class EmployeeModel {

    private String empId;
    private String position;
    private String name;
    private String phone;
    private String email;
    private String region;
    private String branch;
    private boolean IsChecked = false;


    public boolean isChecked() {
        return IsChecked;
    }

    public void setChecked(boolean checked) {
        IsChecked = checked;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
