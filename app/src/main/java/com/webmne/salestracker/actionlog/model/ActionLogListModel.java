package com.webmne.salestracker.actionlog.model;

import com.webmne.salestracker.api.model.Response;

/**
 * Created by vatsaldesai on 14-09-2016.
 */
public class ActionLogListModel extends com.webmne.salestracker.api.model.Response{

    private ActionLogDataModel data;

    public ActionLogDataModel getData() {
        return data;
    }

    public void setData(ActionLogDataModel data) {
        this.data = data;
    }
}
