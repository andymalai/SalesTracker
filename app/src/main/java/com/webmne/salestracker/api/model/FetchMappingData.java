package com.webmne.salestracker.api.model;

import java.io.Serializable;

/**
 * Created by priyasindkar on 06-10-2016.
 */

public class FetchMappingData implements Serializable{

    public String MappingId, UserId, Date, Mapping, MappingVisit;

    public FetchMappingData() {
        Mapping = "";
        MappingId = "";
        MappingVisit = "";
    }
}
