package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 01-09-2016.
 */
public class LoginResponse extends Response {

    private UserProfile Data;

    public UserProfile getData() {
        return Data;
    }

    public void setData(UserProfile data) {
        Data = data;
    }
}
