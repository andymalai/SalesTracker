package com.webmne.salestracker.contacts.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.webmne.salestracker.R;
import com.webmne.salestracker.contacts.adapter.DepartmentContactListAdapter;
import com.webmne.salestracker.contacts.model.DepartmentContactModel;
import com.webmne.salestracker.contacts.model.DepartmentContactSubModel;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.databinding.FragmentBranchContactBinding;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class DepartmentContactFragment extends Fragment {

    FragmentBranchContactBinding fragmentBranchContactBinding;

    private DepartmentContactListAdapter departmentContactListAdapter;
    private List<Object> departmentContactModelList;

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
        fragmentBranchContactBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_branch_contact, container, false);

        View rootVew = fragmentBranchContactBinding.getRoot();

        init();

        return rootVew;
    }


    private void init() {

        initRecyclerView();

        getBranchContact();

        actionListener();
    }


    private void actionListener() {
        fragmentBranchContactBinding.branchContactRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {


            }
        });

        fragmentBranchContactBinding.branchContactRecyclerView.setOnScrollListener(new FamiliarRecyclerViewOnScrollListener(fragmentBranchContactBinding.branchContactRecyclerView.getLayoutManager()) {
            @Override
            public void onScrolledToTop() {

            }

            @Override
            public void onScrolledToBottom() {
                Toast.makeText(getActivity(), "Load more..", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getBranchContact() {

        List<Object> list = new ArrayList<>();
        list.add(new DepartmentContactModel("Department1", null, null, null, DepartmentContactModel.DEPT_TYPE));
        list.add(new DepartmentContactSubModel("SubModel1", "Name", "8523647853"));
        list.add(new DepartmentContactSubModel("SubModel2", "Name", "8523647853"));
        list.add(new DepartmentContactModel("Department2", null, null, null, DepartmentContactModel.DEPT_TYPE));
        list.add(new DepartmentContactSubModel("SubModel3", "Name", "8523647853"));
        list.add(new DepartmentContactModel("Department3", null, null, null, DepartmentContactModel.DEPT_TYPE));
        list.add(new DepartmentContactSubModel("SubModel3", "Name", "8523647853"));
        list.add(new DepartmentContactSubModel("SubModel4", "Name", "8523647853"));

        departmentContactModelList.addAll(list);

        departmentContactListAdapter.setDepartmentContactList(departmentContactModelList);


//        for (int i = 0; i < 2; i++)
//        {
//            DepartmentContactModel departmentContactModel = new DepartmentContactModel();
//
//            departmentContactModel.setDepartmentName("Department " + i);
//
//            List<DepartmentContactSubModel> departmentContactSubModelList = new ArrayList<>();
//
//            for (int j = 0; j < 2; j++)
//            {
//                DepartmentContactSubModel departmentContactSubModel = new DepartmentContactSubModel();
//                departmentContactSubModel.setName("Name " + j);
//                departmentContactSubModel.setMobile("8523647853");
//                departmentContactSubModel.setEmail("vat@amg.com");
//                departmentContactSubModelList.add(departmentContactSubModel);
//            }
//
//            departmentContactModel.setDepartmentContactSubModel(departmentContactSubModelList);
//
//            departmentContactModelList.add(departmentContactModel);
//        }
//
//        departmentContactListAdapter.setDepartmentContactList(departmentContactModelList);
    }


    private void initRecyclerView() {
        departmentContactModelList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fragmentBranchContactBinding.branchContactRecyclerView.setLayoutManager(layoutManager);
        fragmentBranchContactBinding.branchContactRecyclerView.addItemDecoration(new LineDividerItemDecoration(getActivity()));

        departmentContactListAdapter = new DepartmentContactListAdapter(getActivity(), departmentContactModelList);

        fragmentBranchContactBinding.branchContactRecyclerView.setAdapter(departmentContactListAdapter);
        fragmentBranchContactBinding.branchContactRecyclerView.setHasFixedSize(true);
    }


}
