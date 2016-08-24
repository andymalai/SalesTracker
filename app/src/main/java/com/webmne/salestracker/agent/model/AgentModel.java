package com.webmne.salestracker.agent.model;

import java.io.Serializable;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public class AgentModel implements Serializable {

    private int agentId;
    private String AgentName;

    public String getAgentContactNo() {
        return AgentContactNo;
    }

    public void setAgentContactNo(String agentContactNo) {
        AgentContactNo = agentContactNo;
    }

    public String getAgentEmail() {
        return AgentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        AgentEmail = agentEmail;
    }

    private String AgentContactNo;
    private String AgentEmail;
    private boolean isChecked = false;
    private int color;

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String agentName) {
        AgentName = agentName;
    }
}
