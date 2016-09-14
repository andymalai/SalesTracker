package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 09-09-2016.
 */
public class AddAgentRequest {

    /**
     * AgentName : keshav
     * TierId : 2
     * BranchId : 43
     * MobileNo : 9898989898
     * EmailId : mkt223@amg.com
     * AmgCode : AMG345
     * KruniaCode : KR345
     * Description : Any description
     * UserId : 676
     */

    private String AgentName;
    private int TierId;
    private int BranchId;
    private String MobileNo;
    private String EmailId;
    private String AmgCode;
    private String KruniaCode;
    private String Description;
    private int UserId;

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String AgentName) {
        this.AgentName = AgentName;
    }

    public int getTierId() {
        return TierId;
    }

    public void setTierId(int TierId) {
        this.TierId = TierId;
    }

    public int getBranchId() {
        return BranchId;
    }

    public void setBranchId(int BranchId) {
        this.BranchId = BranchId;
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int UserId) {
        this.UserId = UserId;
    }
}
