package com.webmne.salestracker.event;

import com.webmne.salestracker.employee.model.PositionModel;

import java.util.ArrayList;

/**
 * Created by vatsaldesai on 13-10-2016.
 */
public class PositionData {

    ArrayList<PositionModel> Position;

    public ArrayList<PositionModel> getPosition() {
        return Position;
    }

    public void setPosition(ArrayList<PositionModel> position) {
        Position = position;
    }
}
