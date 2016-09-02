package com.webmne.salestracker.contacts.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.webmne.salestracker.R;
import com.webmne.salestracker.api.AppApi;
import com.webmne.salestracker.contacts.adapter.BranchContactListAdapter;
import com.webmne.salestracker.contacts.model.BranchContactModel;
import com.webmne.salestracker.contacts.model.BranchContactsModel;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.databinding.FragmentBranchContactBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchContactFragment extends Fragment {

    AppApi appApi;

    FragmentBranchContactBinding fragmentBranchContactBinding;

    private BranchContactListAdapter branchContactListAdapter;
    private ArrayList<BranchContactsModel> branchContactsModelList;

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

        appApi = MyApplication.getRetrofit().create(AppApi.class);

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
        Call<BranchContactModel> call = appApi.getBranchContact("json", PrefUtils.getBranchId(getActivity()));

        call.enqueue(new Callback<BranchContactModel>() {
            @Override
            public void onResponse(Call<BranchContactModel> call, Response<BranchContactModel> response) {

                if (response.body() != null) {
                    Log.e("response", MyApplication.getGson().toJson(response.body(), BranchContactModel.class));

                    if (response.body().getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        branchContactsModelList.clear();

                        branchContactsModelList.addAll(response.body().getData().getContacts());

                        branchContactListAdapter.setBranchContactList(branchContactsModelList);
                    } else {



                    }


                }


            }

            @Override
            public void onFailure(Call<BranchContactModel> call, Throwable t) {

                Log.e("tag", "t:-" + t);

            }
        });

    }


    private void initRecyclerView() {
        branchContactsModelList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fragmentBranchContactBinding.branchContactRecyclerView.setLayoutManager(layoutManager);
        fragmentBranchContactBinding.branchContactRecyclerView.addItemDecoration(new LineDividerItemDecoration(getActivity()));

        branchContactListAdapter = new BranchContactListAdapter(getActivity(), branchContactsModelList);

        fragmentBranchContactBinding.branchContactRecyclerView.setAdapter(branchContactListAdapter);
        fragmentBranchContactBinding.branchContactRecyclerView.setHasFixedSize(true);
    }


}
