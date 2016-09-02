package com.webmne.salestracker.contacts.model;

import java.util.ArrayList;

/**
 * Created by vatsaldesai on 01-09-2016.
 */
public class BranchContactDataModel {

    ArrayList<BranchContactsModel> Contacts;

    public ArrayList<BranchContactsModel> getContacts() {
        return Contacts;
    }

    public void setContacts(ArrayList<BranchContactsModel> contacts) {
        Contacts = contacts;
    }
}
