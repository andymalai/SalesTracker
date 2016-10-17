package com.webmne.salestracker.employee.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.employee.model.EmployeeModel;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.EmployeeViewHolder> {

    private Context context;
    private ArrayList<EmployeeModel> employeeList;
    private Animation flipAnim, flipReverse;
    private onSelectionListener onSelectionListener;

    public void setEmployeeList(ArrayList<EmployeeModel> employeeList) {
        this.employeeList = new ArrayList<>();
        this.employeeList = employeeList;
        notifyDataSetChanged();
    }

    public EmployeeListAdapter(Context context, ArrayList<EmployeeModel> employeeList, onSelectionListener onSelectionListener) {
        this.context = context;
        this.employeeList = employeeList;
        this.onSelectionListener = onSelectionListener;
        flipAnim = AnimationUtils.loadAnimation(context, R.anim.flip_anim);
        flipReverse = AnimationUtils.loadAnimation(context, R.anim.flip_anim);
    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_employee_list, parent, false);
        return new EmployeeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        EmployeeModel employeeModel = employeeList.get(position);
        holder.setEmployeeDetails(employeeModel);
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TfTextView txtName, letterIcon, txtLocaion;
        ImageView imgCheck, imgCall, imgEmail;
        LinearLayout parentView;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            txtName = (TfTextView) itemView.findViewById(R.id.txtName);
            letterIcon = (TfTextView) itemView.findViewById(R.id.letterIcon);
            txtLocaion = (TfTextView) itemView.findViewById(R.id.txtLocaion);
            imgCheck = (ImageView) itemView.findViewById(R.id.imgCheck);
            imgCall = (ImageView) itemView.findViewById(R.id.imgCall);
            imgEmail = (ImageView) itemView.findViewById(R.id.imgEmail);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);
        }

        public void setEmployeeDetails(final EmployeeModel employeeModel) {

            txtName.setText(employeeModel.getName());
            letterIcon.setText(employeeModel.getName().substring(0, 1));

            txtLocaion.setText(String.format("%s, %s", employeeModel.getBranch(), employeeModel.getRegion()));

            if (TextUtils.isEmpty(employeeModel.getPhone())) {
                imgCall.setVisibility(View.GONE);
            } else {
                imgCall.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(employeeModel.getEmailId())) {
                imgEmail.setVisibility(View.GONE);
            } else {
                imgEmail.setVisibility(View.VISIBLE);
            }

            if (employeeModel.isChecked()) {
                imgCheck.setVisibility(View.VISIBLE);
                letterIcon.setVisibility(View.GONE);
                parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
            } else {
                imgCheck.setVisibility(View.GONE);
                letterIcon.setVisibility(View.VISIBLE);
                parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }

            imgCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reverseFlip();
                }

                private void reverseFlip() {
                    //PrefUtils.setAgent(context, agentModel);
                    employeeModel.setChecked(false);
                    onSelectionListener.onSelect();
                    imgCheck.startAnimation(flipReverse);
                    parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    flipReverse.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imgCheck.setVisibility(View.GONE);
                            letterIcon.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

            });

            letterIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flip();
                }

                private void flip() {
                    // PrefUtils.setAgent(context, agentModel);
                    employeeModel.setChecked(true);
                    onSelectionListener.onSelect();
                    parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
                    letterIcon.startAnimation(flipAnim);
                    flipAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            letterIcon.setVisibility(View.GONE);
                            imgCheck.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            imgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Functions.makePhoneCall(context, employeeModel.getPhone());
                }
            });

            imgEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Functions.sendMailTo(context, employeeModel.getEmailId());
                }
            });
        }
    }

    public interface onSelectionListener {
        void onSelect();
    }

    public void searchFilter(String searchQuery) {
        final ArrayList<EmployeeModel> filterEmployee = new ArrayList<>();
        for (EmployeeModel model : employeeList) {
            final String text = model.getName().toLowerCase();
            if (text.contains(searchQuery.toLowerCase())) {
                filterEmployee.add(model);
            }
        }
        setEmployeeList(filterEmployee);
    }

    public ArrayList<EmployeeModel> getEmployeeList() {
        return employeeList;
    }
}
