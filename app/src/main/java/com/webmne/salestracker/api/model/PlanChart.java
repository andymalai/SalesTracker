package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class PlanChart {

    /**
     * Date : 2016-10-01
     * PlannedVisits : 0
     * ActualVisited : 0
     * PlanAttainment : 0%
     */

    private String Date;
    private String PlannedVisits;
    private String ActualVisited;
    private String PlanAttainment;

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getPlannedVisits() {
        return PlannedVisits;
    }

    public void setPlannedVisits(String PlannedVisits) {
        this.PlannedVisits = PlannedVisits;
    }

    public String getActualVisited() {
        return ActualVisited;
    }

    public void setActualVisited(String ActualVisited) {
        this.ActualVisited = ActualVisited;
    }

    public String getPlanAttainment() {
        return PlanAttainment;
    }

    public void setPlanAttainment(String PlanAttainment) {
        this.PlanAttainment = PlanAttainment;
    }
}
