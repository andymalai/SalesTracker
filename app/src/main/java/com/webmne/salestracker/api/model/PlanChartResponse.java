package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class PlanChartResponse extends Response {

    private PlanChartData data;

    public PlanChartData getData() {
        return data;
    }

    public void setData(PlanChartData data) {
        this.data = data;
    }
}
