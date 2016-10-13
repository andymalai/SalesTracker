package com.webmne.salestracker.event.model;

/**
 * Created by vatsaldesai on 13-10-2016.
 */
public class Region {

    /**
     * Region : 19
     * RegionId : Maharashtra
     */

    private String Region;
    private String RegionId;

    public String getRegion() {
        return Region;
    }

    public void setRegion(String Region) {
        this.Region = Region;
    }

    public String getRegionId() {
        return RegionId;
    }

    public void setRegionId(String RegionId) {
        this.RegionId = RegionId;
    }

    @Override
    public String toString() {
        return RegionId;
    }
}
