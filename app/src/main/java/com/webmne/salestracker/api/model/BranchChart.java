package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class BranchChart {

    /**
     * Branch : Vadodara
     * Completed : 0
     * OnGoing : 0
     * Overdue : 0
     */

    private String Branch;
    private String Completed;
    private String OnGoing;
    private String Overdue;

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String Branch) {
        this.Branch = Branch;
    }

    public String getCompleted() {
        return Completed;
    }

    public void setCompleted(String Completed) {
        this.Completed = Completed;
    }

    public String getOnGoing() {
        return OnGoing;
    }

    public void setOnGoing(String OnGoing) {
        this.OnGoing = OnGoing;
    }

    public String getOverdue() {
        return Overdue;
    }

    public void setOverdue(String Overdue) {
        this.Overdue = Overdue;
    }
}
