package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 04-10-2016.
 */

public class SalesPlanResponse extends Response {

    private PlanDataResponse data;

    public PlanDataResponse getData() {
        return data;
    }

    public void setData(PlanDataResponse data) {
        this.data = data;
    }
}
