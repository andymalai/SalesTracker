package com.webmne.salestracker.helper;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public enum TileId {

    AGENTS(1),

    CONTACTS(2),

    ACTION_LOG(3),

    SALES_VISIT_PLAN(4),

    EMPLOYEE(5),

    MANAGE_VISIT_PLAN(6),

    TARGET(7);

    TileId(int id) {
        this.id = id;
    }

    int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
