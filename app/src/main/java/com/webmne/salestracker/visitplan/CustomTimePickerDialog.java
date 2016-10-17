package com.webmne.salestracker.visitplan;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.webmne.salestracker.R;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.widget.TfButton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by vatsaldesai on 30-09-2016.
 */

public class CustomTimePickerDialog extends MaterialDialog {

    private Context context;
    private CustomTimePickerCallBack customTimePickerCallBack;
    private MaterialDialog materialDialog;

    private NumberPicker np_hour, np_minute;
    private TfButton btnCancel, btnOk;

    private ArrayList<String> hourList;
    private ArrayList<String> minuteList;

    private int hourNo;

    private String strHour, strMinute, str_flag, strSelectedStartTime, strSelectedEndTime;

    public CustomTimePickerDialog(Builder builder, Context context, String str_flag, String strSelectedStartTime, String strSelectedEndTime, CustomTimePickerCallBack customTimePickerCallBack) {
        super(builder);

        this.context = context;
        this.customTimePickerCallBack = customTimePickerCallBack;
        this.str_flag = str_flag;
        this.strSelectedStartTime = strSelectedStartTime;
        this.strSelectedEndTime = strSelectedEndTime;

        init(builder);
    }

    private void init(Builder builder) {

        materialDialog = builder
                .title(R.string.time_picker_dialog_title)
                .typeface(Functions.getBoldFont(context), Functions.getRegularFont(context))
                .customView(R.layout.custom_time_picker_dialog, false)
                .canceledOnTouchOutside(false)
                .show();

        View view = materialDialog.getCustomView();

        np_hour = (NumberPicker) view.findViewById(R.id.np_hour);
        np_minute = (NumberPicker) view.findViewById(R.id.np_minute);
        btnCancel = (TfButton) view.findViewById(R.id.btnCancel);
        btnOk = (TfButton) view.findViewById(R.id.btnOk);


        actionListener();

        setHourPickerData();

        setMinutePickerData();
    }

    private void actionListener() {

        np_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                strHour = hourList.get(newVal);

            }
        });

        np_minute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                strMinute = minuteList.get(newVal);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customTimePickerCallBack.timePickerCallBack(strHour, strMinute);

                materialDialog.dismiss();

            }
        });


    }

    private void setHourPickerData() {
        hourList = new ArrayList<>();

        int hourCount = 8;
        for (int i = 0; i < 13; i++) {

            hourList.add(String.format("%02d", hourCount));

            if (str_flag.equals("s")) {
                String[] newStartTime = strSelectedStartTime.split(":");

                if (newStartTime[0].equals(hourList.get(i))) {
                    hourNo = i;
                    strHour = hourList.get(i);
                }
            } else if (str_flag.equals("e")) {
                String[] newEndTime = strSelectedEndTime.split(":");

                if (newEndTime[0].equals(hourList.get(i))) {
                    hourNo = i;
                    strHour = hourList.get(i);
                }
            }

            hourCount++;
        }

        np_hour.setMinValue(0);
        np_hour.setMaxValue(hourList.size() - 1);
        np_hour.setWrapSelectorWheel(true);

        String[] hourStringArray = new String[0];
        hourStringArray = hourList.toArray(hourStringArray);

        np_hour.setDisplayedValues(hourStringArray);

        np_hour.setValue(hourNo);
    }

    private void setMinutePickerData() {
        minuteList = new ArrayList<>();

        minuteList.add("00");
        minuteList.add("30");

        np_minute.setMinValue(0);
        np_minute.setMaxValue(minuteList.size() - 1);
        np_minute.setWrapSelectorWheel(true);

        String[] minuteStringArray = new String[0];
        minuteStringArray = minuteList.toArray(minuteStringArray);

        np_minute.setDisplayedValues(minuteStringArray);

        if (str_flag.equals("s")) {
            String[] newEndTime = strSelectedStartTime.split(":");

            if (newEndTime[1].equals("00")) {
                np_minute.setValue(0);
                strMinute = minuteList.get(0);
            }
            if (newEndTime[1].equals("30")) {
                np_minute.setValue(1);
                strMinute = minuteList.get(1);
            }
        } else if (str_flag.equals("e")) {
            String[] newEndTime = strSelectedEndTime.split(":");

            if (newEndTime[1].equals("00")) {
                np_minute.setValue(0);
                strMinute = minuteList.get(0);
            }
            if (newEndTime[1].equals("30")) {
                np_minute.setValue(1);
                strMinute = minuteList.get(1);
            }
        }

    }


}
