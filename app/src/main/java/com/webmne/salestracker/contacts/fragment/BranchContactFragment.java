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
import com.webmne.salestracker.contacts.adapter.BranchContactListAdapter;
import com.webmne.salestracker.contacts.model.BranchContactModel;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.databinding.FragmentBranchContactBinding;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

import java.util.ArrayList;

public class BranchContactFragment extends Fragment {

    FragmentBranchContactBinding fragmentBranchContactBinding;

    private BranchContactListAdapter branchContactListAdapter;
    private ArrayList<BranchContactModel> branchContactModelList;

    public BranchContactFragment() {
        // Required empty public constructor
    }

    public static BranchContactFragment newInstance() {
        BranchContactFragment fragment = new BranchContactFragment();
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
        for (int i = 1; i <= 10; i++) {
            BranchContactModel branchContactModel = new BranchContactModel();
            branchContactModel.setEmpId("" + i);
            branchContactModel.setName("Name " + i);
            branchContactModel.setBranch("Ahmedabad");
            branchContactModel.setRegion("Gujarat");
            branchContactModel.setPhone("8523647853");
            branchContactModel.setEmail("vat@amg.com");
            branchContactModel.setEmpPosition("RM");
            branchContactModelList.add(branchContactModel);
        }
        branchContactListAdapter.setBranchContactList(branchContactModelList);
    }


    private void initRecyclerView() {
        branchContactModelList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fragmentBranchContactBinding.branchContactRecyclerView.setLayoutManager(layoutManager);
        fragmentBranchContactBinding.branchContactRecyclerView.addItemDecoration(new LineDividerItemDecoration(getActivity()));

        branchContactListAdapter = new BranchContactListAdapter(getActivity(), branchContactModelList);

        fragmentBranchContactBinding.branchContactRecyclerView.setAdapter(branchContactListAdapter);
        fragmentBranchContactBinding.branchContactRecyclerView.setHasFixedSize(true);
    }


}
