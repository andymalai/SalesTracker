package com.webmne.salestracker.api.model;

import com.webmne.salestracker.employee.model.EmployeeModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sagartahelyani on 17-10-2016.
 */

public class EmployeeDataResponse implements Serializable {

    private ArrayList<EmployeeModel> Employee;

    public ArrayList<EmployeeModel> getEmployee() {
        return Employee;
    }

    public void setEmployee(ArrayList<EmployeeModel> employee) {
        Employee = employee;
    }
}
