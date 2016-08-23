package com.webmne.salestracker.contacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.contacts.model.BranchContactModel;
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
        LinearLayout parentView;
        ImageView iv_info;

        public BranchContactViewHolder(View itemView) {
            super(itemView);
            txtMarketerName = (TfTextView) itemView.findViewById(R.id.txtMarketerName);
            txtPosition = (TfTextView) itemView.findViewById(R.id.txtPosition);
            txtBranchRegion = (TfTextView) itemView.findViewById(R.id.txtBranchRegion);
            letterIcon = (TfTextView) itemView.findViewById(R.id.letterIcon);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);
            iv_info = (ImageView) itemView.findViewById(R.id.iv_info);

        }

        public void setBranchContactDetails(final BranchContactModel branchContactModel, final int pos) {

            txtMarketerName.setText(branchContactModel.getName());
            txtPosition.setText(branchContactModel.getEmpPosition());
            txtBranchRegion.setText(String.format("%s , %s", branchContactModel.getBranch(), branchContactModel.getRegion()));
            letterIcon.setText(branchContactModel.getName().substring(0, 1));
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
