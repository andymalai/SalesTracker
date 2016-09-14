package com.webmne.salestracker.contacts.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.contacts.model.BranchContactModel;
import com.webmne.salestracker.contacts.model.BranchContactsModel;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public class BranchContactListAdapter extends RecyclerView.Adapter<BranchContactListAdapter.BranchContactViewHolder> {

    private Context context;
    private ArrayList<BranchContactsModel> branchContactsModelList;

    public void setBranchContactList(ArrayList<BranchContactsModel> branchContactsModelList) {
//        this.branchContactsModelList = new ArrayList<>();
        this.branchContactsModelList = branchContactsModelList;
        notifyDataSetChanged();
    }

    public BranchContactListAdapter(Context context, ArrayList<BranchContactsModel> branchContactsModelList) {
        this.context = context;
        this.branchContactsModelList = branchContactsModelList;
    }

    @Override
    public BranchContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.sample_row_branch_contact, parent, false);
        return new BranchContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BranchContactViewHolder holder, int position) {
        BranchContactsModel branchContactsModel = branchContactsModelList.get(position);
        holder.setBranchContactDetails(branchContactsModel, position);
    }

    @Override
    public int getItemCount() {
        return branchContactsModelList.size();
    }

    class BranchContactViewHolder extends RecyclerView.ViewHolder {

        TfTextView txtMarketerName, txtPosition, txtBranchRegion, letterIcon;
        ImageView ivCall, ivEmail;
        LinearLayout parentView;

        public BranchContactViewHolder(View itemView) {
            super(itemView);
            txtMarketerName = (TfTextView) itemView.findViewById(R.id.txtMarketerName);
            txtPosition = (TfTextView) itemView.findViewById(R.id.txtPosition);
            txtBranchRegion = (TfTextView) itemView.findViewById(R.id.txtBranchRegion);
            letterIcon = (TfTextView) itemView.findViewById(R.id.letterIcon);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);
            ivCall = (ImageView) itemView.findViewById(R.id.ivCall);
            ivEmail = (ImageView) itemView.findViewById(R.id.ivEmail);

        }

        public void setBranchContactDetails(final BranchContactsModel branchContactsModel, final int pos) {

            txtMarketerName.setText(branchContactsModel.getName());
            txtPosition.setText(String.format("(%s)", branchContactsModel.getPosition()));
            txtBranchRegion.setText(String.format("%s , %s", branchContactsModel.getBranchName(), branchContactsModel.getRegion()));
            letterIcon.setText(branchContactsModel.getName().substring(0, 1));

            if (TextUtils.isEmpty(branchContactsModel.getMobileNo())) {
                ivCall.setVisibility(View.GONE);
            } else {
                ivCall.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(branchContactsModel.getEmailid())) {
                ivEmail.setVisibility(View.GONE);
            } else {
                ivEmail.setVisibility(View.VISIBLE);
            }

            ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Functions.makePhoneCall(context, branchContactsModel.getMobileNo());

                }
            });

            ivEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Functions.sendMailTo(context, branchContactsModel.getEmailid());

                }
            });


/*
            iv_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new MaterialDialog.Builder(context)
                            .title(R.string.information)
                            .customView(R.layout.custom_dialog_branch_contact_info, true)
                            .positiveText(R.string.ok)
                            .show();

                }
            });
*/


        }
    }


    public void searchFilter(String searchQuery)
    {
        ArrayList<BranchContactsModel> ModelList = new ArrayList<>();
        for (BranchContactsModel model : branchContactsModelList)
        {
            String text = model.getName().toLowerCase();
            if (text.toLowerCase().trim().contains(searchQuery.toLowerCase().trim())) {
                ModelList.add(model);
            }
        }
        setBranchContactList(ModelList);
    }


}
