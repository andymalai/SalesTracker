package com.webmne.salestracker.api.model;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 15-09-2016.
 */
public class InChargeDataResponse {

    private ArrayList<InCharge> DepartmentPic;

    public ArrayList<InCharge> getDepartmentPic() {
        return DepartmentPic;
    }

    public void setDepartmentPic(ArrayList<InCharge> departmentPic) {
        DepartmentPic = departmentPic;
    }
}
