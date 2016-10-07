package com.webmne.salestracker.api.model;

import java.io.Serializable;

/**
 * Created by sagartahelyani on 04-10-2016.
 */

public class SalesPlanResponse extends Response implements Serializable{

    private PlanDataResponse data;

    public PlanDataResponse getData() {
        return data;
    }

    public void setData(PlanDataResponse data) {
        this.data = data;
    }
}
