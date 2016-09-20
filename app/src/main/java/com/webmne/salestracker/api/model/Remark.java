package com.webmne.salestracker.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 14-09-2016.
 */
public class Remark {

    @SerializedName("Remarks")
    private String Description;

    @SerializedName("Name")
    private String Name;

    @SerializedName("Date")
    private String Date;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
