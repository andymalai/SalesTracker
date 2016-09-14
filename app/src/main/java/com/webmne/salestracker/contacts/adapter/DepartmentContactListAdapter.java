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
import android.widget.TextView;

import com.webmne.salestracker.R;
import com.webmne.salestracker.contacts.model.DepartmentContactContactsModel;
import com.webmne.salestracker.contacts.model.DepartmentContactDetail;
import com.webmne.salestracker.contacts.model.DepartmentContactModel;
import com.webmne.salestracker.contacts.model.DepartmentContactSubDetail;
import com.webmne.salestracker.contacts.model.DepartmentContactSubModel;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;

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
        if (o instanceof DepartmentContactDetail) {
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
                if (o instanceof DepartmentContactDetail) {
                    departmentViewHolder.setDepartment((DepartmentContactDetail) o);
                }
                break;
            case DEPT_SUB_TYPE:
                DepartmentSubViewHolder departmentSubViewHolder = (DepartmentSubViewHolder) holder;
                if (o instanceof DepartmentContactSubDetail) {
                    departmentSubViewHolder.setSubDepartment((DepartmentContactSubDetail) o);
                }
                break;
        }

    }


    @Override
    public int getItemCount() {

        return departmentContactModelList.size();
    }


    // SectionViewHolder Class for Sections
    public class DepartmentViewHolder extends RecyclerView.ViewHolder {

        final TextView txtDepartment;

        public DepartmentViewHolder(View itemView) {
            super(itemView);

            txtDepartment = (TextView) itemView.findViewById(R.id.txtDepartment);

        }

        public void setDepartment(DepartmentContactDetail o) {
            txtDepartment.setText(o.getDeptName());
        }
    }

    // ItemViewHolder Class for Items in each Section
    public class DepartmentSubViewHolder extends RecyclerView.ViewHolder {

        final TextView txtName;
        ImageView ivMobile, ivEmail;
        LinearLayout parentView;

        public DepartmentSubViewHolder(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            ivMobile = (ImageView) itemView.findViewById(R.id.ivMobile);
            ivEmail = (ImageView) itemView.findViewById(R.id.ivEmail);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);

        }

        public void setSubDepartment(final DepartmentContactSubDetail o)
        {
            txtName.setText(o.getName());

            if (TextUtils.isEmpty(o.getPhone()))
            {
                ivMobile.setVisibility(View.GONE);
            } else {
                ivMobile.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(o.getEmail()))
            {
                ivEmail.setVisibility(View.GONE);
            } else {
                ivEmail.setVisibility(View.VISIBLE);
            }

            ivMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Functions.makePhoneCall(context, o.getPhone());

                }
            });

            ivEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Functions.sendMailTo(context, o.getEmail());

                }
            });

        }
    }


    public void searchFilter(String searchQuery)
    {
        List<Object> modelList = new ArrayList<>();

        for (int i=0; i<departmentContactModelList.size(); i++)
        {
            if(departmentContactModelList.get(i) instanceof DepartmentContactDetail)
            {
                String text = ((DepartmentContactDetail) departmentContactModelList.get(i)).getDeptName();

                if (text.toLowerCase().trim().contains(searchQuery.toLowerCase().trim()))
                {
                    modelList.add(departmentContactModelList.get(i));

                    String str_dept_contact_id = ((DepartmentContactDetail) departmentContactModelList.get(i)).getDeptId();

                    for (int j=0; j<departmentContactModelList.size(); j++)
                    {
                        if(departmentContactModelList.get(j) instanceof DepartmentContactSubDetail) {

                            String str_sub_dept_contact_id = ((DepartmentContactSubDetail) departmentContactModelList.get(j)).getDept_id();

                            if (str_dept_contact_id.equals(str_sub_dept_contact_id)) {
                                modelList.add(departmentContactModelList.get(j));
                            }
                        }
                    }

                }

            }

        }
        setDepartmentContactList(modelList);
    }



}
