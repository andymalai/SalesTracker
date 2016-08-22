package com.webmne.salestracker.contacts.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmne.salestracker.R;

public class DepartmentContactFragment extends Fragment {

    public DepartmentContactFragment() {
        // Required empty public constructor
    }

    public static DepartmentContactFragment newInstance() {
        DepartmentContactFragment fragment = new DepartmentContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootVew = inflater.inflate(R.layout.fragment_branch_contact, container, false);


        return rootVew;
    }

}
