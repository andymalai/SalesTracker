package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 01-09-2016.
 */
public class BaseResponse {

    /**
     * ResponseCode : 1
     * ResponseMsg : Login successfully
     */

    private String ResponseCode;
    private String ResponseMsg;

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String ResponseCode) {
        this.ResponseCode = ResponseCode;
    }

    public String getResponseMsg() {
        return ResponseMsg;
    }

    public void setResponseMsg(String ResponseMsg) {
        this.ResponseMsg = ResponseMsg;
    }
}
