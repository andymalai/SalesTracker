package com.webmne.salestracker.api.model;

import java.io.Serializable;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public class AgentModel implements Serializable {

    /**
     * Agentid : 122
     * Name : AGT2
     * Tierid : 1
     * Branchid : 44
     * RegionName : 17
     * Description : Description
     * AmgCode : AMG346
     * KruniaCode : KR346
     * MobileNo : 1111111111
     * Emailid : AG@AMG.COM
     * Checked : false
     */

    private String Agentid;
    private String Name;
    private String Tierid;
    private String Branchid;
    private String BranchName;
    private String RegionName;
    private String AmgCode;
    private String KruniaCode;
    private String MobileNo;
    private String Emailid;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    private String Description;
    private boolean IsChecked = false;

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String branchName) {
        BranchName = branchName;
    }

    public boolean isChecked() {
        return IsChecked;
    }

    public void setChecked(boolean checked) {
        IsChecked = checked;
    }

    public String getAgentid() {
        return Agentid;
    }

    public void setAgentid(String Agentid) {
        this.Agentid = Agentid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getTierid() {
        return Tierid;
    }

    public void setTierid(String Tierid) {
        this.Tierid = Tierid;
    }

    public String getBranchid() {
        return Branchid;
    }

    public void setBranchid(String Branchid) {
        this.Branchid = Branchid;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String Region) {
        this.RegionName = Region;
    }

    public String getAmgCode() {
        return AmgCode;
    }

    public void setAmgCode(String AmgCode) {
        this.AmgCode = AmgCode;
    }

    public String getKruniaCode() {
        return KruniaCode;
    }

    public void setKruniaCode(String KruniaCode) {
        this.KruniaCode = KruniaCode;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String MobileNo) {
        this.MobileNo = MobileNo;
    }

    public String getEmailid() {
        return Emailid;
    }

    public void setEmailid(String Emailid) {
        this.Emailid = Emailid;
    }

    @Override
    public String toString() {
        return Name;
    }
}
