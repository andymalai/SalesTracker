package com.webmne.salestracker.employee.model;

/**
 * Created by vatsaldesai on 26-09-2016.
 */

public class PositionModel {

    /**
     * PositionId : 7
     * PositionName : AAS
     */

    private String PositionId;
    private String PositionName;

    public PositionModel(String positionId, String positionName) {
        PositionId = positionId;
        PositionName = positionName;
    }

    public String getPositionId() {
        return PositionId;
    }

    public void setPositionId(String PositionId) {
        this.PositionId = PositionId;
    }

    public String getPositionName() {
        return PositionName;
    }

    public void setPositionName(String PositionName) {
        this.PositionName = PositionName;
    }

    @Override
    public String toString() {
        return PositionName;
    }
}
