package com.webmne.salestracker.contacts.model;

import android.databinding.BaseObservable;

import com.webmne.salestracker.api.model.Response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vatsaldesai on 19-08-2016.
 */
public class BranchContactModel extends Response {

    BranchContactDataModel Data;

    public BranchContactDataModel getData() {
        return Data;
    }

    public void setData(BranchContactDataModel data) {
        Data = data;
    }
}
