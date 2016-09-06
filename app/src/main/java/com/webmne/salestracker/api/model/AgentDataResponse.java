package com.webmne.salestracker.api.model;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 02-09-2016.
 */
public class AgentDataResponse {

    public ArrayList<AgentModel> getAgents() {
        return Agents;
    }

    public void setAgents(ArrayList<AgentModel> agents) {
        Agents = agents;
    }

    private ArrayList<AgentModel> Agents;


}
