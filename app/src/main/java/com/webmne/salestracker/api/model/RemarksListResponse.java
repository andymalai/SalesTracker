package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 14-09-2016.
 */
public class RemarksListResponse extends Response {

    private RemarksDataResponse data;

    public RemarksDataResponse getData() {
        return data;
    }

    public void setData(RemarksDataResponse data) {
        this.data = data;
    }
}
