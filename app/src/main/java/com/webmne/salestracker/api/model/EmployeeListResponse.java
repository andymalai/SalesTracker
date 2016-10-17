package com.webmne.salestracker.api.model;

import java.io.Serializable;

/**
 * Created by sagartahelyani on 17-10-2016.
 */

public class EmployeeListResponse extends Response implements Serializable{

    private EmployeeDataResponse data;

    public EmployeeDataResponse getData() {
        return data;
    }

    public void setData(EmployeeDataResponse data) {
        this.data = data;
    }
}
