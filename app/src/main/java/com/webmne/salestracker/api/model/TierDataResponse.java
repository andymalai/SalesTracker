package com.webmne.salestracker.api.model;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 02-09-2016.
 */
public class TierDataResponse {

    private ArrayList<Tier> Tiers;

    public ArrayList<Tier> getTiers() {
        return Tiers;
    }

    public void setTiers(ArrayList<Tier> tiers) {
        Tiers = tiers;
    }
}
