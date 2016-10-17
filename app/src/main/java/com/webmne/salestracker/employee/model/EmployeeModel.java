package com.webmne.salestracker.employee.model;

import java.io.Serializable;

/**
 * Created by vatsaldesai on 23-09-2016.
 */

public class EmployeeModel implements Serializable {


    /**
     * Name : MKT_guj_002
     * Position : 9
     * Phone : 9724470522
     * EmailId : ermanojkolhe@gmail.com
     * Branch : 51
     * Region : 20
     * IsChecked : true
     */

    private String Name;
    private String Position;
    private String Phone;
    private String EmailId;
    private String Branch;
    private String Region;
    private boolean IsChecked;

    public boolean isChecked() {
        return IsChecked;
    }

    public void setChecked(boolean checked) {
        IsChecked = checked;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String Position) {
        this.Position = Position;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String EmailId) {
        this.EmailId = EmailId;
    }

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String Branch) {
        this.Branch = Branch;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String Region) {
        this.Region = Region;
    }
}
