package com.webmne.salestracker.api.model;

import com.webmne.salestracker.communication.Communication;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sagartahelyani on 12-10-2016.
 */

public class CommunicationResponse extends Response implements Serializable {

    private ArrayList<Communication> Communication;

    public ArrayList<com.webmne.salestracker.communication.Communication> getCommunication() {
        return Communication;
    }

    public void setCommunication(ArrayList<com.webmne.salestracker.communication.Communication> communication) {
        Communication = communication;
    }
}
