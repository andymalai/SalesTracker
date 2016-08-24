package com.webmne.salestracker.ui.dashboard.model;

import android.content.Context;

import com.webmne.salestracker.R;
import com.webmne.salestracker.helper.TileId;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 11-08-2016.
 */
public class HomeTileConfiguration {

    private Context context;

    private String[] marketNames = {"Agents", "Contacts", "Action Log", "Sales Visit Plan", "Employee", "Manage Visit Plan", "Target"};

    //    private String[] colors = {"#bb6666", "#af4c4c", "#af4c4c", "#bb6666", "#bb6666", "#af4c4c", "#af4c4c"};
    private String[] colors = {"#ffffff", "#f1f1f1", "#f1f1f1", "#ffffff", "#ffffff", "#f1f1f1", "#f1f1f1"};

    private String[] bgColors = {"#ffffff", "#d8d8d8", "#d8d8d8", "#ffffff", "#ffffff", "#d8d8d8", "#ffffff"};

    private int[] ids = {TileId.AGENTS.getId(), TileId.CONTACTS.getId(), TileId.ACTION_LOG.getId(), TileId.SALES_VISIT_PLAN.getId(), TileId.EMPLOYEE.getId(),
            TileId.MANAGE_VISIT_PLAN.getId(), TileId.TARGET.getId()};

    private int[] icons = {R.drawable.ic_agent, R.drawable.ic_pick_contact, R.drawable.ic_action_log, R.drawable.ic_plan, R.drawable.ic_employee,
            R.drawable.ic_manage, R.drawable.ic_target};

    public HomeTileConfiguration(Context context) {
        this.context = context;
    }

    public ArrayList<HomeTileBean> getDashboardOptions(int id) {

        ArrayList<HomeTileBean> arr = new ArrayList<>();

        // depends on Id, change methods
        arr = getMarketOptions();

        return arr;
    }

    public ArrayList<HomeTileBean> getMarketOptions() {
        ArrayList<HomeTileBean> arr = new ArrayList<>();

        for (int i = context.getResources().getInteger(R.integer.market_start); i <= context.getResources().getInteger(R.integer.market_end); i++) {
            HomeTileBean tile = new HomeTileBean();
            tile.setTileName(marketNames[i]);
            tile.setBackgroundColor(colors[i]);
            tile.setContentColor(colors[i]);
            tile.setId(ids[i]);
            tile.setTileIcon(icons[i]);
            arr.add(tile);
        }
        return arr;
    }

    public ArrayList<HomeTileBean> getHeadOptions() {
        ArrayList<HomeTileBean> arr = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            HomeTileBean tile = new HomeTileBean();
            tile.setTileName(marketNames[i]);
            tile.setBackgroundColor(colors[i]);
            tile.setId(ids[i]);
            tile.setTileIcon(icons[i]);
            arr.add(tile);
        }
        return arr;
    }

    public ArrayList<HomeTileBean> getBranchOptions() {
        ArrayList<HomeTileBean> arr = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            HomeTileBean tile = new HomeTileBean();
            tile.setTileName(marketNames[i]);
            tile.setBackgroundColor(colors[i]);
            tile.setId(ids[i]);
            tile.setTileIcon(icons[i]);
            arr.add(tile);
        }
        return arr;
    }

    public ArrayList<HomeTileBean> getRegionOptions() {
        ArrayList<HomeTileBean> arr = new ArrayList<>();

        for (int i = 0; i < marketNames.length; i++) {
            HomeTileBean tile = new HomeTileBean();
            tile.setTileName(marketNames[i]);
            tile.setBackgroundColor(colors[i]);
            tile.setId(ids[i]);
            tile.setTileIcon(icons[i]);
            arr.add(tile);
        }
        return arr;
    }
}


/*

MARKET: Agents, Contacts, Action Log, Sales Visit Plan

Head of Sales(HOS): MARKET ++ {Employee, Manage Visit Plan}

Branch Manager(BM): HOS ++ {Target}

Region: HOS -- {Agent, Target}

*/
