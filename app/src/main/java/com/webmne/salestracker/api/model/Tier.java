package com.webmne.salestracker.api.model;

/**
 * Created by sagartahelyani on 02-09-2016.
 */
public class Tier {

    /**
     * Teirid : 1
     * TierName : STAR
     */

    private String Teirid;
    private String TierName;

    public String getTeirid() {
        return Teirid;
    }

    public void setTeirid(String Teirid) {
        this.Teirid = Teirid;
    }

    public String getTierName() {
        return TierName;
    }

    public void setTierName(String TierName) {
        this.TierName = TierName;
    }

    @Override
    public String toString() {
        return TierName;
    }
}
