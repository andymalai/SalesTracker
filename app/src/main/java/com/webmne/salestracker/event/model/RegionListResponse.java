package com.webmne.salestracker.event.model;

import com.webmne.salestracker.api.model.Response;

import java.util.ArrayList;

/**
 * Created by vatsaldesai on 13-10-2016.
 */

public class RegionListResponse extends Response {

    RegionData data;

    public RegionData getData() {
        return data;
    }

    public void setData(RegionData data) {
        this.data = data;
    }
}
