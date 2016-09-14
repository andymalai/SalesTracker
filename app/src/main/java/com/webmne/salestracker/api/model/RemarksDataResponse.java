package com.webmne.salestracker.api.model;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 14-09-2016.
 */
public class RemarksDataResponse {

    private ArrayList<Remark> Remarks;

    public ArrayList<Remark> getRemarks() {
        return Remarks;
    }

    public void setRemarks(ArrayList<Remark> remarks) {
        Remarks = remarks;
    }
}
