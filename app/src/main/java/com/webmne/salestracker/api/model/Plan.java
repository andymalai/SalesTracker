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

    public Plan(){

    }

    public Plan(String planId, String agentName, String agentId, String startTime, String endTime, String remark, String status) {
        PlanId = planId;
        AgentName = agentName;
        AgentId = agentId;
        StartTime = startTime;
        EndTime = endTime;
        Remark = remark;
        Status = status;
    }

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

    String[] timeArray = {"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00",
            "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00",
            "17:30", "18:00", "18:30", "19:00", "19:30"};

    public int getPosition() {
        int pos = 0;
        try {
            for (int i = 0; i < timeArray.length; i++) {
                if (getStartTime().substring(0, getStartTime().length() - 3).equals(timeArray[i])) {
                    pos = i;
                }
            }
        }catch (Exception e){
             pos = 0;
        }
        return pos;
    }

}
