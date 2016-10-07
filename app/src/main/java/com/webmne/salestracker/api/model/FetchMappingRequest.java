package com.webmne.salestracker.api.model;

/**
 * Created by priyasindkar on 06-10-2016.
 */

public class FetchMappingRequest {
    public String UserId;
    public String Date;

    public FetchMappingRequest(String userId, String date) {
        UserId = userId;
        Date = date;
    }
}
