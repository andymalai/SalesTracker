package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class SlaChart {


    /**
     * Department : Hub Operations
     * AverageSLA : 3
     * NoOfIssues : 20
     */

    private String Department;
    private String AverageSLA;
    private String NoOfIssues;

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String Department) {
        this.Department = Department;
    }

    public String getAverageSLA() {
        return AverageSLA;
    }

    public void setAverageSLA(String AverageSLA) {
        this.AverageSLA = AverageSLA;
    }

    public String getNoOfIssues() {
        return NoOfIssues;
    }

    public void setNoOfIssues(String NoOfIssues) {
        this.NoOfIssues = NoOfIssues;
    }
}
