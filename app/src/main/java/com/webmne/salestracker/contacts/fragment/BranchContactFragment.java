package com.webmne.salestracker.contacts.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.AppApi;
import com.webmne.salestracker.contacts.adapter.BranchContactListAdapter;
import com.webmne.salestracker.contacts.model.BranchContactModel;
import com.webmne.salestracker.contacts.model.BranchContactsModel;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.FragmentBranchContactBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchContactFragment extends Fragment {

    AppApi appApi;

    FragmentBranchContactBinding fragmentBranchContactBinding;

    private BranchContactListAdapter branchContactListAdapter;
    private ArrayList<BranchContactsModel> branchContactsModelList;

    private LoadingIndicatorDialog dialog;

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
        fragmentBranchContactBinding.relativeLayout.setVisibility(View.GONE);
        showProgress();

        Call<BranchContactModel> call = appApi.getBranchContact(PrefUtils.getBranchId(getActivity()));

        call.enqueue(new Callback<BranchContactModel>() {
            @Override
            public void onResponse(Call<BranchContactModel> call, Response<BranchContactModel> response) {
                fragmentBranchContactBinding.relativeLayout.setVisibility(View.VISIBLE);
                dismissProgress();
                if (response.isSuccessful()) {
                    Log.e("response", MyApplication.getGson().toJson(response.body(), BranchContactModel.class));

                    if (response.body().getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        branchContactsModelList.addAll(response.body().getData().getContacts());

                        branchContactListAdapter.setBranchContactList(branchContactsModelList);

                    } else {
                        SimpleToast.error(getActivity(), response.body().getResponse().getResponseMsg());
                    }

                } else {
                    SimpleToast.error(getActivity(), getString(R.string.try_again));
                }
            }

            @Override
            public void onFailure(Call<BranchContactModel> call, Throwable t) {
                fragmentBranchContactBinding.relativeLayout.setVisibility(View.VISIBLE);
                dismissProgress();
                if (t instanceof TimeoutException) {
                    SimpleToast.error(getActivity(), getString(R.string.time_out), getString(R.string.fa_error));
                } else if (t instanceof UnknownHostException) {
                    SimpleToast.error(getActivity(), getString(R.string.no_internet_connection), getString(R.string.fa_error));
                } else {
                    SimpleToast.error(getActivity(), getString(R.string.try_again), getString(R.string.fa_error));
                }
            }
        });

    }


    public void searchBranchContact(String text)
    {
        if (TextUtils.isEmpty(text))
        {
            branchContactListAdapter.setBranchContactList(branchContactsModelList);
        }
        else
        {
            branchContactListAdapter.searchFilter(text);
        }
    }


    private void initRecyclerView() {
        branchContactsModelList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fragmentBranchContactBinding.branchContactRecyclerView.setLayoutManager(layoutManager);
        fragmentBranchContactBinding.branchContactRecyclerView.addItemDecoration(new LineDividerItemDecoration(getActivity()));

        fragmentBranchContactBinding.branchContactRecyclerView.setEmptyView(fragmentBranchContactBinding.emptyLayout);
        fragmentBranchContactBinding.emptyLayout.setContent("No Branches");

        branchContactListAdapter = new BranchContactListAdapter(getActivity(), branchContactsModelList);

        fragmentBranchContactBinding.branchContactRecyclerView.setAdapter(branchContactListAdapter);
        fragmentBranchContactBinding.branchContactRecyclerView.setHasFixedSize(true);
    }

    public void showProgress() {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(getActivity(), "Loading...", android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }


}
