package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 04-10-2016.
 */

public class Plan {


    /**
     * PlanId : 197
     * AgentName : Masum Chauhan
     * AgentId : 150
     * StartTime : 09:00:00
     * EndTime : 10:00:00
     * Remark : 0
     * Status : 0
     */

    private String PlanId;
    private String AgentName;
    private String AgentId;
    private String StartTime;
    private String EndTime;
    private String Remark;
    private String Status;

    public String getPlanId() {
        return PlanId;
    }

    public void setPlanId(String PlanId) {
        this.PlanId = PlanId;
    }

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String AgentName) {
        this.AgentName = AgentName;
    }

    public String getAgentId() {
        return AgentId;
    }

    public void setAgentId(String AgentId) {
        this.AgentId = AgentId;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }
}
