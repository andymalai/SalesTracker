package com.webmne.salestracker.contacts.model;

/**
 * Created by vatsaldesai on 01-09-2016.
 */
public class BranchContactsModel {

    /**
     * EmpId : 1
     * Region : Gujarat
     * Position : Marketer
     * Name : MKT_guj_001
     * MobileNo : 9898989898
     * EmailId : mkt223@amg.com
     * BranchId : 36
     * BranchName : Vadodara
     */

    private int EmpId;
    private String Region;
    private String Position;
    private String Name;
    private String MobileNo;
    private String EmailId;
    private int BranchId;
    private String BranchName;

    public int getEmpId() {
        return EmpId;
    }

    public void setEmpId(int EmpId) {
        this.EmpId = EmpId;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String Region) {
        this.Region = Region;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String Position) {
        this.Position = Position;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String MobileNo) {
        this.MobileNo = MobileNo;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String EmailId) {
        this.EmailId = EmailId;
    }

    public int getBranchId() {
        return BranchId;
    }

    public void setBranchId(int BranchId) {
        this.BranchId = BranchId;
    }

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String BranchName) {
        this.BranchName = BranchName;
    }
}
