package com.webmne.salestracker.visitplan.model;

import java.io.Serializable;

/**
 * Created by sagartahelyani on 19-10-2016.
 */

public class SelectedUser implements Serializable {

    private String userId;
    private String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
