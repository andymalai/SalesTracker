package com.webmne.salestracker.event.model;

import com.google.gson.annotations.SerializedName;
import com.webmne.salestracker.api.model.Response;

import java.io.Serializable;

/**
 * Created by vatsaldesai on 11-10-2016.
 */
public class Event implements Serializable {

    /**
     * Id : 70
     * UserID : 709
     * EventDate : 2016-10-07
     * Title : tRAINING
     * Description : tRAINING DAY
     * RegionId : 0
     * BranchId : 47,49,50,51,53,54,
     * RoleId : 7,3,1,2,5,8,6,11,9,4,12,
     */

    private String Id;
    private String UserID;
    private String EventDate;
    private String Title;
    private String Description;
    private String RegionId;
    private String BranchId;
    private String RoleId;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public String getEventDate() {
        return EventDate;
    }

    public void setEventDate(String EventDate) {
        this.EventDate = EventDate;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getRegionId() {
        return RegionId;
    }

    public void setRegionId(String RegionId) {
        this.RegionId = RegionId;
    }

    public String getBranchId() {
        return BranchId;
    }

    public void setBranchId(String BranchId) {
        this.BranchId = BranchId;
    }

    public String getRoleId() {
        return RoleId;
    }

    public void setRoleId(String RoleId) {
        this.RoleId = RoleId;
    }
}
