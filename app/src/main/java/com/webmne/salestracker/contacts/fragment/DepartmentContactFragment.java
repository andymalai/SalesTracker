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

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.AppApi;
import com.webmne.salestracker.contacts.adapter.DepartmentContactListAdapter;
import com.webmne.salestracker.contacts.model.DepartmentContactModel;
import com.webmne.salestracker.custom.DepartmentContactsImpl;
import com.webmne.salestracker.databinding.FragmentBranchContactBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentContactFragment extends Fragment {

    AppApi appApi;

    FragmentBranchContactBinding fragmentBranchContactBinding;

    private DepartmentContactListAdapter departmentContactListAdapter;
    private List<Object> departmentContactModelList;

    DepartmentContactsImpl departmentContacts;

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

        departmentContacts = new DepartmentContactsImpl();
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
        Call<DepartmentContactModel> call = appApi.getDepartmentContact();

        call.enqueue(new Callback<DepartmentContactModel>() {
            @Override
            public void onResponse(Call<DepartmentContactModel> call, Response<DepartmentContactModel> response) {

                if (response.isSuccessful()) {
                    Log.e("response", MyApplication.getGson().toJson(response.body(), DepartmentContactModel.class));

                    DepartmentContactModel contactModel = response.body();

                    if (contactModel.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        departmentContactModelList = departmentContacts.getSortedDepartmentList(contactModel.getData().getContacts());

                        departmentContactListAdapter.setDepartmentContactList(departmentContactModelList);

                    } else {
                        SimpleToast.error(getActivity(), response.body().getResponse().getResponseMsg());
                    }

                } else {
                    SimpleToast.error(getActivity(), getString(R.string.try_again));
                }

            }

            @Override
            public void onFailure(Call<DepartmentContactModel> call, Throwable t) {

            }
        });


    }


    private void initRecyclerView() {
        departmentContactModelList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fragmentBranchContactBinding.branchContactRecyclerView.setLayoutManager(layoutManager);
//        fragmentBranchContactBinding.branchContactRecyclerView.addItemDecoration(new LineDividerItemDecoration(getActivity()));

        departmentContactListAdapter = new DepartmentContactListAdapter(getActivity(), departmentContactModelList);

        fragmentBranchContactBinding.branchContactRecyclerView.setAdapter(departmentContactListAdapter);
        fragmentBranchContactBinding.branchContactRecyclerView.setHasFixedSize(true);
    }


}
