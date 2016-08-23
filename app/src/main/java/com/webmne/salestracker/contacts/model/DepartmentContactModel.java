package com.webmne.salestracker.contacts.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vatsaldesai on 22-08-2016.
 */
public class DepartmentContactModel implements Serializable {

    public static final int DEPT_TYPE = 0;
    public static final int DEPT_SUB_TYPE = 1;

    private String departmentName, name, mobile, email;
    private int mType;

    public DepartmentContactModel(String departmentName, String name, String mobile, String email, int type) {
        this.departmentName = departmentName;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.mType = type;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }
}