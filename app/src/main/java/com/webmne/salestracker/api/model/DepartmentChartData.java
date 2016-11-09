package com.webmne.salestracker.api.model;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class DepartmentChartData {

    private ArrayList<DepartmentChart> Department;

    public ArrayList<DepartmentChart> getDepartmentData() {
        return Department;
    }

    public void setDepartmentData(ArrayList<DepartmentChart> department) {
        Department = department;
    }
}
