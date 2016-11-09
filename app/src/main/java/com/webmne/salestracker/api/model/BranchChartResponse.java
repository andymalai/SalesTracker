package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class BranchChartResponse extends Response {

    private BranchChartData data;

    public BranchChartData getData() {
        return data;
    }

    public void setData(BranchChartData data) {
        this.data = data;
    }
}
