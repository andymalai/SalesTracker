package com.webmne.salestracker.event;

import com.webmne.salestracker.api.model.Response;

/**
 * Created by vatsaldesai on 13-10-2016.
 */
public class PositionListResponse extends com.webmne.salestracker.api.model.Response{

    PositionData data;

    public PositionData getData() {
        return data;
    }

    public void setData(PositionData data) {
        this.data = data;
    }
}
