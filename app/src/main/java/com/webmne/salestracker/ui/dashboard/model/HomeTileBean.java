package com.webmne.salestracker.ui.dashboard.model;

import android.graphics.Color;

/**
 * Created by sagartahelyani on 11-08-2016.
 */
public class HomeTileBean {

    private int id;
    private String tileName;
    private int tileIcon;
    private String backgroundColor;

    public int getContentColor() {
        return Color.parseColor(contentColor);
    }

    public void setContentColor(String contentColor) {
        this.contentColor = contentColor;
    }

    private String contentColor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBackgroundColor() {
        return Color.parseColor(backgroundColor);
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public HomeTileBean() {

    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public int getTileIcon() {
        return tileIcon;
    }

    public void setTileIcon(int tileIcon) {
        this.tileIcon = tileIcon;
    }
}
