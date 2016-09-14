package com.webmne.salestracker.actionlog.model;

import com.webmne.salestracker.api.model.Response;

/**
 * Created by vatsaldesai on 14-09-2016.
 */
public class DepartmentListModel extends com.webmne.salestracker.api.model.Response{

    DepartmentData data;

    public DepartmentData getData() {
        return data;
    }

    public void setData(DepartmentData data) {
        this.data = data;
    }
}
