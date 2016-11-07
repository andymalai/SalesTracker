package com.webmne.salestracker.visitplan.model;

import com.webmne.salestracker.api.model.Response;

/**
 * Created by vatsaldesai on 02-11-2016.
 */
public class FetchRemarkResponse extends com.webmne.salestracker.api.model.Response{

    private FetchRemarkData data;

    public FetchRemarkData getData() {
        return data;
    }

    public void setData(FetchRemarkData data) {
        this.data = data;
    }
}
