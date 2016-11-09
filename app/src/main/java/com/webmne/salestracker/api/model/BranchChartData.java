package com.webmne.salestracker.api.model;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class BranchChartData {

    public ArrayList<BranchChart> getBranchData() {
        return Branch;
    }

    public void setBranchData(ArrayList<BranchChart> branch) {
        Branch = branch;
    }

    private ArrayList<BranchChart> Branch;
}
