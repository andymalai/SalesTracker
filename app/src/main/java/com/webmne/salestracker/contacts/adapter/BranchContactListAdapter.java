package com.webmne.salestracker.contacts.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.contacts.model.BranchContactModel;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public class BranchContactListAdapter extends RecyclerView.Adapter<BranchContactListAdapter.BranchContactViewHolder> {

    private Context context;
    private ArrayList<BranchContactModel> branchContactModelList;

    public void setBranchContactList(ArrayList<BranchContactModel> branchContactModelList) {
        this.branchContactModelList = new ArrayList<>();
        this.branchContactModelList = branchContactModelList;
        notifyDataSetChanged();
    }

    public BranchContactListAdapter(Context context, ArrayList<BranchContactModel> branchContactModelList) {
        this.context = context;
        this.branchContactModelList = branchContactModelList;
    }

    @Override
    public BranchContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.sample_row_branch_contact, parent, false);
        return new BranchContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BranchContactViewHolder holder, int position) {
        BranchContactModel branchContactModel = branchContactModelList.get(position);
        holder.setBranchContactDetails(branchContactModel, position);
    }

    @Override
    public int getItemCount() {
        return branchContactModelList.size();
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

        public void setBranchContactDetails(final BranchContactModel branchContactModel, final int pos) {

            txtMarketerName.setText(branchContactModel.getName());
            txtPosition.setText(branchContactModel.getEmpPosition());
            txtBranchRegion.setText(String.format("%s , %s", branchContactModel.getBranch(), branchContactModel.getRegion()));
            letterIcon.setText(branchContactModel.getName().substring(0, 1));

            if (TextUtils.isEmpty(branchContactModel.getPhone())) {
                ivCall.setVisibility(View.GONE);
            } else {
                ivCall.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(branchContactModel.getEmail())) {
                ivEmail.setVisibility(View.GONE);
            } else {
                ivEmail.setVisibility(View.VISIBLE);
            }

            ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Functions.makePhoneCall(context, branchContactModel.getPhone());

                }
            });

            ivEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Functions.sendMailTo(context, branchContactModel.getEmail());

                    String[] TO = {branchContactModel.getEmail()};
                    String[] CC = {""};
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);

                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                    emailIntent.putExtra(Intent.EXTRA_CC, CC);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

                    try {
                        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                    }

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

}
