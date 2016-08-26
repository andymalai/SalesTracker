package com.webmne.salestracker.ui.model;

/**
 * Created by sagartahelyani on 26-08-2016.
 */
public class UserProfile {


    /**
     * CallingCode : 650
     * Email : mkt2@amg.com
     * FirstName : MKT_guj_001
     * IsEmailVerified : true
     * IsMobileVerified : true
     * Mobile : 555555
     * branch : 28
     * ProfilePic : null
     * ProfilePicURL : sites/default/files/userpic/
     * ResponseCode : 1
     * ResponseMsg : Login successfully
     */

    private String CallingCode;
    private String Email;
    private String FirstName;
    private String IsEmailVerified;
    private String IsMobileVerified;
    private String Mobile;
    private String branch;
    private Object ProfilePic;
    private String ProfilePicURL;
    private String ResponseCode;
    private String ResponseMsg;

    public String getCallingCode() {
        return CallingCode;
    }

    public void setCallingCode(String CallingCode) {
        this.CallingCode = CallingCode;
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

    public String getIsEmailVerified() {
        return IsEmailVerified;
    }

    public void setIsEmailVerified(String IsEmailVerified) {
        this.IsEmailVerified = IsEmailVerified;
    }

    public String getIsMobileVerified() {
        return IsMobileVerified;
    }

    public void setIsMobileVerified(String IsMobileVerified) {
        this.IsMobileVerified = IsMobileVerified;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Object getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(Object ProfilePic) {
        this.ProfilePic = ProfilePic;
    }

    public String getProfilePicURL() {
        return ProfilePicURL;
    }

    public void setProfilePicURL(String ProfilePicURL) {
        this.ProfilePicURL = ProfilePicURL;
    }

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
