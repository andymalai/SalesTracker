package com.webmne.salestracker.contacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webmne.salestracker.R;
import com.webmne.salestracker.contacts.model.DepartmentContactModel;
import com.webmne.salestracker.contacts.model.DepartmentContactSubModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vatsaldesai on 22-08-2016.
 */
public class DepartmentContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<Object> departmentContactModelList;

    private final int DEPT_TYPE = 0, DEPT_SUB_TYPE = 1;

    public DepartmentContactListAdapter(Context context, List<Object> departmentContactModel) {

        this.departmentContactModelList = departmentContactModel;
        this.context = context;
    }

    public void setDepartmentContactList(List<Object> departmentContactModel) {
        this.departmentContactModelList = new ArrayList<>();
        this.departmentContactModelList = departmentContactModel;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        Object o = departmentContactModelList.get(position);
        if (o instanceof DepartmentContactModel) {

            return DEPT_TYPE;
        } else {
            return DEPT_SUB_TYPE;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case DEPT_TYPE:
                View v1 = inflater.inflate(R.layout.sample_row_department_contact_header_section, parent, false);
                viewHolder = new DepartmentViewHolder(v1);
                break;
            case DEPT_SUB_TYPE:
                View v2 = inflater.inflate(R.layout.sample_row_department_contact_sub_section, parent, false);
                viewHolder = new DepartmentSubViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object o = departmentContactModelList.get(position);
        switch (holder.getItemViewType()) {
            case DEPT_TYPE:
                DepartmentViewHolder departmentViewHolder = (DepartmentViewHolder) holder;
                if (o instanceof DepartmentContactModel) {
                    departmentViewHolder.setDepartment((DepartmentContactModel) o);
                }
                break;
            case DEPT_SUB_TYPE:
                DepartmentSubViewHolder departmentSubViewHolder = (DepartmentSubViewHolder) holder;
                if (o instanceof DepartmentContactSubModel) {
                    departmentSubViewHolder.setSubDepartment((DepartmentContactSubModel) o);
                }
               // departmentSubViewHolder.setDepartmentSub(departmentSubViewHolder, position);
                break;
        }

    }


//    @Override
//    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
//
//        DepartmentViewHolder departmentViewHolder = (DepartmentViewHolder) holder;
//        DepartmentContactModel departmentContactModel = departmentContactModelList.get(section);
//        departmentViewHolder.setDepartment(departmentContactModel);
//    }

//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, int relativePosition, int absolutePosition) {
//
//        DepartmentSubViewHolder departmentSubViewHolder = (DepartmentSubViewHolder) holder;
//        List<DepartmentContactSubModel> subList = departmentContactModelList.get(section).getDepartmentContactSubModel();
//
//        DepartmentContactSubModel departmentContactSubModel = subList.get(relativePosition);
//        departmentSubViewHolder.setDepartmentSub(departmentContactSubModel);
//
//    }


    @Override
    public int getItemCount() {

        Log.e("tag", "departmentContactModelList.size():-" + departmentContactModelList.size());

        return departmentContactModelList.size();
    }


    // SectionViewHolder Class for Sections
    public class DepartmentViewHolder extends RecyclerView.ViewHolder {

        final TextView txtDepartment;

        public DepartmentViewHolder(View itemView) {
            super(itemView);

            txtDepartment = (TextView) itemView.findViewById(R.id.txtDepartment);

        }

        //        public void setDepartment(DepartmentContactModel departmentContactModel) {
        public void setDepartment(DepartmentViewHolder holder, int position) {

            DepartmentContactModel departmentContactModel = (DepartmentContactModel) departmentContactModelList.get(position);

            if (departmentContactModel != null) {
                Log.e("tag", "departmentContactModel.getDepartmentName():-" + departmentContactModel.getDepartmentName());

                txtDepartment.setText(departmentContactModel.getDepartmentName());
            }

        }

        public void setDepartment(DepartmentContactModel o) {
            txtDepartment.setText(o.getDepartmentName());
        }
    }

    // ItemViewHolder Class for Items in each Section
    public class DepartmentSubViewHolder extends RecyclerView.ViewHolder {

        final TextView txtName, txtMobile, txtEmail;
        LinearLayout parentView;

        public DepartmentSubViewHolder(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtMobile = (TextView) itemView.findViewById(R.id.txtMobile);
            txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);

        }

        //        public void setDepartmentSub(DepartmentContactSubModel departmentContactSubModel) {
        public void setDepartmentSub(DepartmentSubViewHolder holder, int position) {

            DepartmentContactModel departmentContactModel = (DepartmentContactModel) departmentContactModelList.get(position);

            if (departmentContactModel != null) {
                Log.e("tag", "departmentContactModel.getName():-" + departmentContactModel.getName());
                Log.e("tag", "departmentContactModel.getMobile():-" + departmentContactModel.getMobile());
                Log.e("tag", "departmentContactModel.getEmail():-" + departmentContactModel.getEmail());

                txtName.setText(departmentContactModel.getName());
                txtMobile.setText(departmentContactModel.getMobile());
                txtEmail.setText(departmentContactModel.getEmail());
            }

        }

        public void setSubDepartment(DepartmentContactSubModel o) {
            txtName.setText(o.getName());
        }
    }

}
