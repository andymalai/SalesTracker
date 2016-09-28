package com.webmne.salestracker.ui.dashboard.model;

import com.webmne.salestracker.R;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.TileId;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 11-08-2016.
 */
public class HomeTileConfiguration {

    // marketer grid options
    private String[] marketNames = {"Agents", "Contacts", "Action Log", "Sales Visit Plan"};
    private int[] marketIds = {TileId.AGENTS.getId(), TileId.CONTACTS.getId(), TileId.ACTION_LOG.getId(), TileId.SALES_VISIT_PLAN.getId()};
    private int[] marketIcons = {R.drawable.ic_agent, R.drawable.ic_pick_contact, R.drawable.ic_action_log, R.drawable.ic_plan};

    // HOS grid options
    private String[] hosNames = {"Agents", "Contacts", "Action Log", "Sales Visit Plan", "Employee"};
    private int[] hosIds = {TileId.AGENTS.getId(), TileId.CONTACTS.getId(), TileId.ACTION_LOG.getId(), TileId.SALES_VISIT_PLAN.getId(), TileId.EMPLOYEE.getId()};
    private int[] hosIcons = {R.drawable.ic_agent, R.drawable.ic_pick_contact, R.drawable.ic_action_log, R.drawable.ic_plan, R.drawable.ic_employee};

    // BM grid options
    private String[] bmNames = {"Agents", "Contacts", "Action Log", "Sales Visit Plan", "Employee"};
    private int[] bmIds = {TileId.AGENTS.getId(), TileId.CONTACTS.getId(), TileId.ACTION_LOG.getId(), TileId.SALES_VISIT_PLAN.getId(), TileId.EMPLOYEE.getId()};
    private int[] bmIcons = {R.drawable.ic_agent, R.drawable.ic_pick_contact, R.drawable.ic_action_log, R.drawable.ic_plan, R.drawable.ic_employee};

    // RM grid options
    private String[] rmNames = {"Contacts", "Action Log", "Sales Visit Plan", "Employee"};
    private int[] rmIds = {TileId.CONTACTS.getId(), TileId.ACTION_LOG.getId(), TileId.SALES_VISIT_PLAN.getId(), TileId.EMPLOYEE.getId()};
    private int[] rmIcons = {R.drawable.ic_pick_contact, R.drawable.ic_action_log, R.drawable.ic_plan, R.drawable.ic_employee};

    private String[] bgColors = {"#ffffff", "#f1f1f1", "#f1f1f1", "#ffffff", "#ffffff", "#f1f1f1", "#f1f1f1"};

    public ArrayList<HomeTileBean> getDashboardOptions(String position) {

        ArrayList<HomeTileBean> arr = new ArrayList<>();

        // depends on Id, change methods
        if (position.equals(AppConstants.MARKETER)) {
            arr = getMarketOptions();
        } else if (position.equals(AppConstants.HOS)) {
            arr = getHeadOptions();
        } else if (position.equals(AppConstants.BM)) {
            arr = getBranchOptions();
        } else if (position.equals(AppConstants.RM)) {
            arr = getRegionOptions();
        }
        return arr;
    }

    public ArrayList<HomeTileBean> getMarketOptions() {
        ArrayList<HomeTileBean> arr = new ArrayList<>();

        for (int i = 0; i < marketNames.length; i++) {
            HomeTileBean tile = new HomeTileBean();
            tile.setTileName(marketNames[i]);
            tile.setBackgroundColor(bgColors[i]);
            tile.setContentColor(bgColors[i]);
            tile.setId(marketIds[i]);
            tile.setTileIcon(marketIcons[i]);
            arr.add(tile);
        }
        return arr;
    }

    public ArrayList<HomeTileBean> getHeadOptions() {
        ArrayList<HomeTileBean> arr = new ArrayList<>();

        for (int i = 0; i < hosNames.length; i++) {
            HomeTileBean tile = new HomeTileBean();
            tile.setTileName(hosNames[i]);
            tile.setBackgroundColor(bgColors[i]);
            tile.setId(hosIds[i]);
            tile.setTileIcon(hosIcons[i]);
            arr.add(tile);
        }
        return arr;
    }

    public ArrayList<HomeTileBean> getBranchOptions() {
        ArrayList<HomeTileBean> arr = new ArrayList<>();

        for (int i = 0; i < bmNames.length; i++) {
            HomeTileBean tile = new HomeTileBean();
            tile.setTileName(bmNames[i]);
            tile.setBackgroundColor(bgColors[i]);
            tile.setId(bmIds[i]);
            tile.setTileIcon(bmIcons[i]);
            arr.add(tile);
        }
        return arr;
    }

    public ArrayList<HomeTileBean> getRegionOptions() {
        ArrayList<HomeTileBean> arr = new ArrayList<>();

        for (int i = 0; i < rmNames.length; i++) {
            HomeTileBean tile = new HomeTileBean();
            tile.setTileName(rmNames[i]);
            tile.setBackgroundColor(bgColors[i]);
            tile.setId(rmIds[i]);
            tile.setTileIcon(rmIcons[i]);
            arr.add(tile);
        }
        return arr;
    }
}

/*

MARKETER: Agents, Contacts, Action Log, Sales Visit Plan

Head of Sales(HOS): MARKETER ++ {Employee, Manage Visit Plan}

Branch Manager(BM): HOS ++ {Target}

Region: HOS -- {Agent, Target}

*/
