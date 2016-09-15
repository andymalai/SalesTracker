package com.webmne.salestracker.actionlog.model;

/**
 * Created by vatsaldesai on 14-09-2016.
 */
public class DepartmentListResponse extends com.webmne.salestracker.api.model.Response{

    DepartmentData data;

    public DepartmentData getData() {
        return data;
    }

    public void setData(DepartmentData data) {
        this.data = data;
    }
}
