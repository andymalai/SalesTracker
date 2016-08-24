package com.webmne.salestracker.contacts.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vatsaldesai on 22-08-2016.
 */
public class DepartmentContactModel implements Serializable {

    private String departmentName;

    public DepartmentContactModel(String departmentName) {
        this.departmentName = departmentName;
    }


    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}