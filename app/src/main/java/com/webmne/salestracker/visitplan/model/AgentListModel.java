package com.webmne.salestracker.visitplan.model;

/**
 * Created by vatsaldesai on 28-09-2016.
 */
public class AgentListModel {

    private String AgentId;
    private String AgentName;
    private String AgentTier;
    private String TotalPlan;
    private String ActualPlan;
    private String AdditionPlan;

    private boolean isChecked;


    public AgentListModel(String agentId, String agentName, String agentTier, String totalPlan, String actualPlan, String additionPlan) {
        AgentId = agentId;
        AgentName = agentName;
        AgentTier = agentTier;
        TotalPlan = totalPlan;
        ActualPlan = actualPlan;
        AdditionPlan = additionPlan;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getAgentId() {
        return AgentId;
    }

    public void setAgentId(String AgentId) {
        this.AgentId = AgentId;
    }

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String AgentName) {
        this.AgentName = AgentName;
    }

    public String getAgentTier() {
        return AgentTier;
    }

    public void setAgentTier(String AgentTier) {
        this.AgentTier = AgentTier;
    }

    public String getTotalPlan() {
        return TotalPlan;
    }

    public void setTotalPlan(String TotalPlan) {
        this.TotalPlan = TotalPlan;
    }

    public String getActualPlan() {
        return ActualPlan;
    }

    public void setActualPlan(String ActualPlan) {
        this.ActualPlan = ActualPlan;
    }

    public String getAdditionPlan() {
        return AdditionPlan;
    }

    public void setAdditionPlan(String AdditionPlan) {
        this.AdditionPlan = AdditionPlan;
    }
}
