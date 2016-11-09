package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class DepartmentChart {

    /**
     * Department : Hub Operations
     * Completed : 2
     * OnGoing : 3
     * Overdue : 15
     */

    private String Department;
    private String Completed;
    private String OnGoing;
    private String Overdue;

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String Department) {
        this.Department = Department;
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
