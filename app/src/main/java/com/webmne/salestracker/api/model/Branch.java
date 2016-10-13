package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 05-09-2016.
 */
public class Branch {

    /**
     * BranchId : 40
     * BranchName : Nagpur
     * Region : Maharashtra
     */

    private String BranchId;
    private String BranchName;
    private String Region;

    public String getBranchId() {
        return BranchId;
    }

    public void setBranchId(String BranchId) {
        this.BranchId = BranchId;
    }

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String BranchName) {
        this.BranchName = BranchName;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String Region) {
        this.Region = Region;
    }

    @Override
    public String toString() {
        return BranchName;
    }
}
