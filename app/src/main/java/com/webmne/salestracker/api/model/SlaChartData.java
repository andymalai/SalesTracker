package com.webmne.salestracker.api.model;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class SlaChartData {

    private ArrayList<SlaChart> Department;

    public ArrayList<SlaChart> getSlaData() {
        return Department;
    }

    public void setSlaData(ArrayList<SlaChart> department) {
        Department = department;
    }
}
