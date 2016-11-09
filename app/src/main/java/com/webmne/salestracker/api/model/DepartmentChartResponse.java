package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class DepartmentChartResponse extends Response {

    private DepartmentChartData data;

    public DepartmentChartData getData() {
        return data;
    }

    public void setData(DepartmentChartData data) {
        this.data = data;
    }
}