package com.webmne.salestracker.contacts.model;

import android.databinding.BaseObservable;

import com.webmne.salestracker.api.model.Response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vatsaldesai on 19-08-2016.
 */
public class BranchContactModel extends Response {

    BranchContactDataModel data;

    public BranchContactDataModel getData() {
        return data;
    }

    public void setData(BranchContactDataModel data) {
        this.data = data;
    }
}
