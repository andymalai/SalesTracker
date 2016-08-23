package com.webmne.salestracker.actionlog.model;

/**
 * Created by sagartahelyani on 22-08-2016.
 */
public class ActionLogModel {

    private String AgentName;

    private String Description;

    private String DateRaised;

    private String Department;

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    private boolean isCompleted;

    private String LastUpdate;

    private int SLA;

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String agentName) {
        AgentName = agentName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDateRaised() {
        return DateRaised;
    }

    public void setDateRaised(String dateRaised) {
        DateRaised = dateRaised;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getLastUpdate() {
        return LastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        LastUpdate = lastUpdate;
    }

    public int getSLA() {
        return SLA;
    }

    public void setSLA(int SLA) {
        this.SLA = SLA;
    }
}
