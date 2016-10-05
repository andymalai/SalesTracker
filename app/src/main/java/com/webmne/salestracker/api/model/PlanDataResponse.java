package com.webmne.salestracker.api.model;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 04-10-2016.
 */

public class PlanDataResponse {

    private String Target;

    private String Progress;

    private ArrayList<DatePlan> Plans;

    public String getTarget() {
        return Target;
    }

    public void setTarget(String target) {
        Target = target;
    }

    public String getProgress() {
        return Progress;
    }

    public void setProgress(String progress) {
        Progress = progress;
    }

    public ArrayList<DatePlan> getPlans() {
        return Plans;
    }

    public void setPlans(ArrayList<DatePlan> plans) {
        Plans = plans;
    }
}
