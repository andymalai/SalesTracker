package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class SlaChartDataResponse extends Response {

    private SlaChartData data;

    public SlaChartData getData() {
        return data;
    }

    public void setData(SlaChartData data) {
        this.data = data;
    }
}
