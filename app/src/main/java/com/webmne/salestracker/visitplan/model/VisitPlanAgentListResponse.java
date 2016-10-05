package com.webmne.salestracker.visitplan.model;

import java.util.ArrayList;

/**
 * Created by vatsaldesai on 29-09-2016.
 */

public class VisitPlanAgentListResponse extends com.webmne.salestracker.api.model.Response {

    private ArrayList<AgentListModel> data;

    public ArrayList<AgentListModel> getData() {
        return data;
    }

    public void setData(ArrayList<AgentListModel> data) {
        this.data = data;
    }
}
