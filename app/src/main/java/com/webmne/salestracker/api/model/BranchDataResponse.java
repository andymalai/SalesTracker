package com.webmne.salestracker.api.model;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 05-09-2016.
 */
public class BranchDataResponse {

    private ArrayList<Branch> Branches;

    public ArrayList<Branch> getBranches() {
        return Branches;
    }

    public void setBranches(ArrayList<Branch> branches) {
        Branches = branches;
    }
}
