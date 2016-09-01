package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 26-08-2016.
 */
public class UserProfile {

    /**
     * Branch : 44
     * Branch_name : Ankleshwar
     * Email : mkt212@amg.com
     * FirstName : MKT_guj_001
     * Mobile : 555555
     * Pos_name : Marketer
     * Roleid : 9
     * Userid : 676
     */

    private String Branch;
    private String Branch_name;
    private String Email;
    private String FirstName;
    private String Mobile;
    private String Pos_name;
    private String Roleid;
    private String Userid;

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

    public String getPos_name() {
        return Pos_name;
    }

    public void setPos_name(String Pos_name) {
        this.Pos_name = Pos_name;
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
}
