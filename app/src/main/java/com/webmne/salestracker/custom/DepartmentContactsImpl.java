package com.webmne.salestracker.custom;

import com.webmne.salestracker.contacts.model.DepartmentContactContactsModel;
import com.webmne.salestracker.contacts.model.DepartmentContactDetail;
import com.webmne.salestracker.contacts.model.DepartmentContactSubDetail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by vatsaldesai on 05-09-2016.
 */
public class DepartmentContactsImpl {

    List<Object> list = new ArrayList<>();

    ArrayList<DepartmentContactContactsModel> contacts;

    public DepartmentContactsImpl() {

    }

    public List<Object> getSortedDepartmentList(ArrayList<DepartmentContactContactsModel> contacts)
    {
        HashSet<String> departmentId = new HashSet<String>();

        for (int i = 0; i < contacts.size(); i++) {
            departmentId.add(contacts.get(i).getDepartmentid());
        }

        int j = 0;
        for (String deptid : departmentId) {
            list.add(new DepartmentContactDetail(contacts.get(j).getDepartmentid(), contacts.get(j).getDepartmentName()));

            for (int k = 0; k < contacts.size(); k++) {
                if (deptid.equals(contacts.get(k).getDepartmentid())) {
                    list.add(new DepartmentContactSubDetail(contacts.get(j).getDepartmentid(), contacts.get(k).getName(), contacts.get(k).getEmailid(), contacts.get(k).getMobileNo()));
                }
            }

            j++;
        }
        j = 0;

        return list;
    }



}
