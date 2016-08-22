package com.webmne.salestracker.contacts.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.webmne.salestracker.BR;
import com.webmne.salestracker.R;
import com.webmne.salestracker.contacts.model.BranchContactModel;
import com.webmne.salestracker.databinding.SampleRowBranchContactBinding;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public class BranchContactListAdapter extends RecyclerView.Adapter<BranchContactListAdapter.BranchContactViewHolder> {

    private Context context;
    private ArrayList<BranchContactModel> branchContactModelList;
    private onSelectionListener onSelectionListener;

    public void setBranchContactList(ArrayList<BranchContactModel> branchContactModelList) {
        this.branchContactModelList = new ArrayList<>();
        this.branchContactModelList = branchContactModelList;
        notifyDataSetChanged();
    }

    public BranchContactListAdapter(Context context, ArrayList<BranchContactModel> branchContactModelList, onSelectionListener onSelectionListener) {
        this.context = context;
        this.branchContactModelList = branchContactModelList;
        this.onSelectionListener = onSelectionListener;
    }

    @Override
    public BranchContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        SampleRowBranchContactBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.sample_row_branch_contact, parent, false);
        return new BranchContactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BranchContactViewHolder holder, int position) {
    
        //// TODO: 22-08-2016  
        SampleRowBranchContactBinding contactBinding = holder.getViewBinding();
        BranchContactModel branchContactModel = branchContactModelList.get(position);
        holder.setBranchContactDetails(branchContactModel, position);
    }

    @Override
    public int getItemCount() {
        return branchContactModelList.size();
    }

    class BranchContactViewHolder extends RecyclerView.ViewHolder {

        TfTextView txtMarketerName, txtPosition, txtBranchRegion, txtPhone, txtEmail;
        MaterialLetterIcon letterIcon;
        LinearLayout parentView;

        private SampleRowBranchContactBinding viewBinding;

        public BranchContactViewHolder(SampleRowBranchContactBinding binding) {
            super(binding.getRoot());
            viewBinding = binding;
            viewBinding.executePendingBindings();

            txtMarketerName = (TfTextView) itemView.findViewById(R.id.txtMarketerName);
            txtPosition = (TfTextView) itemView.findViewById(R.id.txtPosition);
            txtBranchRegion = (TfTextView) itemView.findViewById(R.id.txtBranchRegion);
            letterIcon = (MaterialLetterIcon) itemView.findViewById(R.id.letterIcon);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);

        }
        public SampleRowBranchContactBinding getViewBinding(){
            return  viewBinding;
        }

        public void setBranchContactDetails(final BranchContactModel branchContactModel, final int pos) {

            txtMarketerName.setText(branchContactModel.getName());
            txtPosition.setText(branchContactModel.getEmpPosition());
            txtBranchRegion.setText(String.format("%s , %s", branchContactModel.getBranch(), branchContactModel.getRegion()));
            txtPhone.setText(branchContactModel.getPhone());
            txtEmail.setText(branchContactModel.getEmail());
            letterIcon.setLetter(branchContactModel.getName().substring(0, 1));
            letterIcon.setShapeColor(branchContactModel.getColor());

            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onSelectionListener.onSelect(pos);

                }
            });


        }
    }

    public interface onSelectionListener {
        void onSelect(int pos);
    }

}
