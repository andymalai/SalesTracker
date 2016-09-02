package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 02-09-2016.
 */
public class AgentListResponse extends BaseResponse {

    private AgentDataResponse data;

    public AgentDataResponse getData() {
        return data;
    }

    public void setData(AgentDataResponse data) {
        this.data = data;
    }
}
