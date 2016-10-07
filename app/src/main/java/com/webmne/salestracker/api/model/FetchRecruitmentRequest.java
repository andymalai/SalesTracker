package com.webmne.salestracker.api.model;

/**
 * Created by priyasindkar on 06-10-2016.
 */

public class FetchRecruitmentRequest {
    public String UserId;
    public String Date;

    public FetchRecruitmentRequest(String userId, String date) {
        UserId = userId;
        Date = date;
    }
}
