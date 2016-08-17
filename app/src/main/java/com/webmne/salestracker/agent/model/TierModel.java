package com.webmne.salestracker.agent.model;

/**
 * Created by sagartahelyani on 15-08-2016.
 */
public class TierModel {

    private int tierId;
    private String tierName;

    public TierModel(int tierId, String tierName) {
        this.tierId = tierId;
        this.tierName = tierName;
    }

    public int getTierId() {
        return tierId;
    }

    public void setTierId(int tierId) {
        this.tierId = tierId;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }
}
