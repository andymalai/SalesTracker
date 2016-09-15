package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 15-09-2016.
 */
public class InChargeListResponse extends Response {

    private InChargeDataResponse data;

    public InChargeDataResponse getData() {
        return data;
    }

    public void setData(InChargeDataResponse data) {
        this.data = data;
    }
}
