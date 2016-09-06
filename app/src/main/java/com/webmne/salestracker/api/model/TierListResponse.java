package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 02-09-2016.
 */
public class TierListResponse extends Response {

    private TierDataResponse data;

    public TierDataResponse getData() {
        return data;
    }

    public void setData(TierDataResponse data) {
        this.data = data;
    }
}
