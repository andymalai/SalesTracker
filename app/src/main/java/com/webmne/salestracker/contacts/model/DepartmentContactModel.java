package com.webmne.salestracker.contacts.model;

import com.webmne.salestracker.api.model.Response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vatsaldesai on 22-08-2016.
 */
public class DepartmentContactModel extends com.webmne.salestracker.api.model.Response {

    DepartmentContactDataModel data;

    public DepartmentContactDataModel getData() {
        return data;
    }

    public void setData(DepartmentContactDataModel data) {
        this.data = data;
    }
}