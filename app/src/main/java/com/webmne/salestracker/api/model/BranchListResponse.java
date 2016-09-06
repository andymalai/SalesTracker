package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 05-09-2016.
 */
public class BranchListResponse extends Response {

    private BranchDataResponse data;

    public BranchDataResponse getData() {
        return data;
    }

    public void setData(BranchDataResponse data) {
        this.data = data;
    }
}
