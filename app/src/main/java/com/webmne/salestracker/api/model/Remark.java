package com.webmne.salestracker.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 14-09-2016.
 */
public class Remark {

    @SerializedName(value = "Description", alternate = {"HosRemark", "BmRemark", "RmRemark", "HqRemark"})
    private String Description;

    @SerializedName(value = "Name", alternate = {"HosName", "BmName", "RmName", "Hqname"})
    private String Name;

    @SerializedName(value = "Position", alternate = {"HosPosition", "BmPosition", "RmPosition", "HqPosition"})
    private String Position;

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

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }
}
