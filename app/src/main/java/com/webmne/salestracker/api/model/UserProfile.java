package com.webmne.salestracker.api.model;

import java.io.Serializable;

/**
 * Created by sagartahelyani on 26-08-2016.
 */
public class UserProfile implements Serializable{

    /**
     * Email : ermanojkolhe@gmail.com
     * FirstName : MKT_guj_001
     * Mobile : 9724470522
     * Branch : 51
     * Branch_name : Ankleshwar
     * RegionId : 20
     * Region : Gujarat
     * Roleid : 9
     * Userid : 709
     * Pos_name : Marketer
     */

    private String Email;
    private String FirstName;
    private String Mobile;
    private String Branch;
    private String Branch_name;
    private String RegionId;
    private String Region;
    private String Roleid;
    private String Userid;
    private String Pos_name;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String Branch) {
        this.Branch = Branch;
    }

    public String getBranch_name() {
        return Branch_name;
    }

    public void setBranch_name(String Branch_name) {
        this.Branch_name = Branch_name;
    }

    public String getRegionId() {
        return RegionId;
    }

    public void setRegionId(String RegionId) {
        this.RegionId = RegionId;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String Region) {
        this.Region = Region;
    }

    public String getRoleid() {
        return Roleid;
    }

    public void setRoleid(String Roleid) {
        this.Roleid = Roleid;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String Userid) {
        this.Userid = Userid;
    }

    public String getPos_name() {
        return Pos_name;
    }

    public void setPos_name(String Pos_name) {
        this.Pos_name = Pos_name;
    }
}
