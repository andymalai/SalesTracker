package com.webmne.salestracker.actionlog.model;

/**
 * Created by vatsaldesai on 14-09-2016.
 */
public class ActionLogListResponse extends com.webmne.salestracker.api.model.Response {

    private ActionLogDataModel data;

    public ActionLogDataModel getData() {
        return data;
    }

    public void setData(ActionLogDataModel data) {
        this.data = data;
    }
}
