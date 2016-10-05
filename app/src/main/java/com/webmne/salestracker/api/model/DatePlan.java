package com.webmne.salestracker.api.model;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 04-10-2016.
 */

public class DatePlan {

    private String Date;

    private String NoPlanRemark;

    private ArrayList<Plan> Plan;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getNoPlanRemark() {
        return NoPlanRemark;
    }

    public void setNoPlanRemark(String noPlanRemark) {
        NoPlanRemark = noPlanRemark;
    }

    public ArrayList<com.webmne.salestracker.api.model.Plan> getPlan() {
        return Plan;
    }

    public void setPlan(ArrayList<com.webmne.salestracker.api.model.Plan> plan) {
        Plan = plan;
    }
}
