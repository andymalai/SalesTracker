package com.webmne.salestracker.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Plan;
import com.webmne.salestracker.databinding.ItemPlanItemBinding;
import com.webmne.salestracker.helper.AppConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sagartahelyani on 23-08-2016.
 */
public class PlanItem extends LinearLayout {

    private Context context;
    private ItemPlanItemBinding binding;
    private Plan plan;
    private Calendar currentDate;
    private Calendar todayCalendar;
    private onPlanChangeListener onPlanChangeListener;

    public void setOnPlanChangeListener(PlanItem.onPlanChangeListener onPlanChangeListener) {
        this.onPlanChangeListener = onPlanChangeListener;
    }

    public PlanItem(Context context, Plan plan, Calendar currentDate) {
        super(context);
        if (!isInEditMode()) {
            this.context = context;
            this.plan = plan;
            this.currentDate = currentDate;
            todayCalendar = Calendar.getInstance();
            init();
        }
    }

    public PlanItem(Context context) {
        super(context);
        if (!isInEditMode()) {
            this.context = context;
            init();
        }
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.item_plan_item, this, true);
        setPlan(plan);
    }

    private void setPlan(final Plan plan) {
        binding.name.setText(String.format(Locale.US, "%s", plan.getAgentName()));
        binding.remark.setText(String.format(Locale.US, "%s", plan.getRemark()));

        Date curDate = new Date();
        Date actualDate = new Date();

        curDate.setTime(todayCalendar.getTimeInMillis());
        actualDate.setTime(currentDate.getTimeInMillis());

        if (curDate.before(actualDate)) {
            binding.actionLayout.setVisibility(View.GONE);

        } else if (curDate.after(actualDate)) {
            binding.actionLayout.setVisibility(View.VISIBLE);

        } else {
            binding.actionLayout.setVisibility(View.VISIBLE);
        }

        binding.parentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlanChangeListener != null) {
                    onPlanChangeListener.onChange(AppConstants.OPEN_REMARK, plan, null);
                }
            }
        });

        if (plan.getStatus().equals(AppConstants.B)) {

            binding.bView.setBackgroundColor(ContextCompat.getColor(context, R.color.tile1));
            binding.oView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
            binding.xView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));

        } else if (plan.getStatus().equals(AppConstants.O)) {

            binding.oView.setBackgroundColor(ContextCompat.getColor(context, R.color.tile1));
            binding.oView.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP);

            binding.bView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));

            binding.xView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
            binding.xView.setColorFilter(ContextCompat.getColor(context, R.color.half_black), PorterDuff.Mode.SRC_ATOP);

        } else if (plan.getStatus().equals(AppConstants.X)) {

            binding.xView.setBackgroundColor(ContextCompat.getColor(context, R.color.tile1));
            binding.xView.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP);

            binding.bView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
            binding.oView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
            binding.oView.setColorFilter(ContextCompat.getColor(context, R.color.half_black), PorterDuff.Mode.SRC_ATOP);
        }

        binding.bView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlanChangeListener != null) {
                    onPlanChangeListener.onChange(AppConstants.UPDATE_BOX, plan, AppConstants.B);
                }
            }
        });

        binding.xView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlanChangeListener != null) {
                    onPlanChangeListener.onChange(AppConstants.UPDATE_BOX, plan, AppConstants.X);
                }
            }
        });

        binding.oView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlanChangeListener != null) {
                    onPlanChangeListener.onChange(AppConstants.UPDATE_BOX, plan, AppConstants.O);
                }
            }
        });

    }

    public interface onPlanChangeListener {
        void onChange(int type, Plan plan, String box);
    }

}
