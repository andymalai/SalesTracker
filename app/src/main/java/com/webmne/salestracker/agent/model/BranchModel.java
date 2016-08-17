package com.webmne.salestracker.agent.model;

/**
 * Created by sagartahelyani on 15-08-2016.
 */
public class BranchModel {

    private int branchId;
    private String branchName;

    public BranchModel(int branchId, String branchName) {
        this.branchId = branchId;
        this.branchName = branchName;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
