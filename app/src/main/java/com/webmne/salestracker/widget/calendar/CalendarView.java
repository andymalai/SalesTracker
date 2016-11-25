package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.DatePlan;
import com.webmne.salestracker.api.model.Plan;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.ConstantFormats;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.visitplan.model.FetchRemarkResponse;
import com.webmne.salestracker.visitplan.model.SelectedUser;
import com.webmne.salestracker.widget.CalendarScrollView;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.TfEditText;
import com.webmne.salestracker.widget.TfTextView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by a7med on 28/06/2015.
 */
public class CalendarView extends LinearLayout {

    // for week days, from-to
    String first, last;

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // how many days to show, defaults to 1 week, 7 days
    private static final int DAYS_COUNT_WEEK = 7;

    private LoadingIndicatorDialog dialog;

    private onGridSelectListener onGridSelectListener;

    private OnCalendarActionClickListener onCalendarActionClickListener;

    public void setOnGridSelectListener(CalendarView.onGridSelectListener onGridSelectListener) {
        this.onGridSelectListener = onGridSelectListener;
    }

    private OnModeChangeListener OnModeChangeListener;

    public void setOnModeChangeListener(CalendarView.OnModeChangeListener onModeChangeListener) {
        OnModeChangeListener = onModeChangeListener;
    }

    public void setOnCalendarActionClickListener(CalendarView.OnCalendarActionClickListener onCalendarActionClickListener) {
        this.onCalendarActionClickListener = onCalendarActionClickListener;
    }

    // default date format
    private static final String DATE_FORMAT = "dd-MMM yyyy";

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    private Context _ctx;

    // internal components
    private LinearLayout header, linearCalenderViewHeader;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TfTextView txtDate, tv_remark;
    private FamiliarRecyclerView grid;
    private RecyclerView timelineRecyclerView;
    private LinearLayout timelineLayout, ll_bottom_layout;
    private CalendarScrollView calendarScrollView;

    //private View blankView;
    private TfButton btnDay, btnMonth, btnDeleteAll, btnRecruitment, btnMapping, btnWeek;
    private ArrayList<TimeLineHour> timeArray;

    private ArrayList<Plan> dayPlans;
    private DayPlanAdapter dayAdapter;

    private ArrayList<DatePlan> monthPlans;
    private MonthAdapter monthAdapter;
    private onMonthChangeListener onCalendarChangeListener;

    private boolean isMonthChange = false;

    private String strRemark;

    private onViewChangeListener onViewChangeListener;

    public void setOnViewChangeListener(CalendarView.onViewChangeListener onViewChangeListener) {
        this.onViewChangeListener = onViewChangeListener;
    }

    public void setOnCalendarChangeListener(onMonthChangeListener onCalendarChangeListener) {
        this.onCalendarChangeListener = onCalendarChangeListener;
    }

    // seasons' rainbow
    int[] rainbow = new int[]{
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    // month-season association (northern hemisphere, sorry australia :)
    int[] monthSeason = new int[]{2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

    public Calendar getCurrentCalendar() {
        return currentDate;
    }

    public void setMonthPlans(ArrayList<DatePlan> monthPlans) {
        this.monthPlans = new ArrayList<>();
        if (monthPlans.size() > 0) {
            this.monthPlans = monthPlans;
        }
        monthAdapter.setPlans(monthPlans);
        if (isMonthChange) {
            setDay();
        }
        notifyAdapter();

        if (this.mode == MODE.DAY) {
            setDay();
        }
    }

    public void setDayPlan(ArrayList<Plan> datePlan) {
        //  dayPlans.addAll(datePlan);

        dayAdapter.setDayPlan(datePlan, currentDate);
    }

    public void notifyAdapter() {
        dayAdapter.notifyDataSetChanged();
        monthAdapter.notifyDataSetChanged();
    }

    public void setUser(SelectedUser selectedUser) {
        this.selectedUser = selectedUser;
        dayAdapter.setSelectedUser(selectedUser);
    }

    public enum MODE {
        DAY,
        WEEK,
        MONTH
    }

    // This is default mode to set as month
    private MODE mode = MODE.MONTH;

    private SelectedUser selectedUser;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_ctx, LinearLayoutManager.VERTICAL, false);

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    public void setMode(MODE mode) {
        this.mode = mode;
        if (OnModeChangeListener != null) {
            OnModeChangeListener.onModeChange(mode);
        }
        updateCalendar();
        if (mode == MODE.DAY) {
            fetchRemarkForBottomLayout();
            ll_bottom_layout.setVisibility(VISIBLE);
        } else if (mode == MODE.MONTH) {
            ll_bottom_layout.setVisibility(GONE);
        }

    }

    private void fetchRemarkForBottomLayout() {

        showProgress(_ctx.getString(R.string.loading));

        String selectedDate = ConstantFormats.ymdFormat.format(currentDate.getTime());

        Log.e("tag", "url1=" + AppConstants.FetchRemarks + PrefUtils.getUserId(_ctx) + "&Date=" + selectedDate);

        new CallWebService(_ctx, AppConstants.FetchRemarks + PrefUtils.getUserId(_ctx) + "&Date=" + selectedDate, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                ll_bottom_layout.setVisibility(View.VISIBLE);

                dismissProgress();

                FetchRemarkResponse getActionLogListResponse = MyApplication.getGson().fromJson(response, FetchRemarkResponse.class);

                if (getActionLogListResponse != null) {

                    if (getActionLogListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        strRemark = getActionLogListResponse.getData().getRemark();
                        tv_remark.setText(String.format("Remark : %s", strRemark));

                    } else {
                        strRemark = "";
                        tv_remark.setText("Remark : No Remark");
                    }

                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                ll_bottom_layout.setVisibility(View.GONE);
                VolleyErrorHelper.showErrorMsg(error, _ctx);
            }

            @Override
            public void noInternet() {
                ll_bottom_layout.setVisibility(View.GONE);
                dismissProgress();
                SimpleToast.error(_ctx, _ctx.getString(R.string.no_internet_connection), _ctx.getString(R.string.fa_error));
            }
        }.call();

    }

    public void showProgress(String string) {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(_ctx, string, android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    /**
     * Load control xml layout
     */
    private void initControl(final Context context, AttributeSet attrs) {

        _ctx = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();
        assignTimelineAdapter();

        dayPlans = new ArrayList<>();
        dayAdapter = new DayPlanAdapter(selectedUser, _ctx, dayPlans, getTimeLineHoursSimple(), currentDate);

        calendarScrollView.setOnScrollChangedListener(new CalendarScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {

                if (t > oldt) {
                    ll_bottom_layout.setVisibility(GONE);
                    if (t <= 0) {
                        ll_bottom_layout.setVisibility(VISIBLE);
                    }
                } else if (t <= 0) {
                    ll_bottom_layout.setVisibility(VISIBLE);
                } else {
                    ll_bottom_layout.setVisibility(VISIBLE);
                    if ((t + calendarScrollView.getHeight() + ll_bottom_layout.getHeight()) == who.getChildAt(0).getHeight()) {
                        ll_bottom_layout.setVisibility(GONE);
                    }
                }

            }
        });

        ll_bottom_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog();
            }
        });

    }

    private void initDialog() {
        final MaterialDialog dialog = new MaterialDialog.Builder(_ctx)
                .title("Remark if No Plan")
                .customView(R.layout.dialog_remark_if_no_plan, true)
                .typeface(Functions.getBoldFont(_ctx), Functions.getRegularFont(_ctx))
                .cancelable(true)
                .build();
        dialog.show();

        final TfEditText edtRemark = (TfEditText) dialog.getCustomView().findViewById(R.id.edtRemark);
        final TfButton btnCancel = (TfButton) dialog.getCustomView().findViewById(R.id.btnCancel);
        final TfButton btnSubmit = (TfButton) dialog.getCustomView().findViewById(R.id.btnSubmit);

        edtRemark.setText(strRemark);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRemark();
            }
        });

    }

    private void submitRemark() {
        // todo call ws

        // submit

       /* if success then
                fetchRemarkForBottomLayout();
        else
            */
    }

    private void assignTimelineAdapter() {
        // get time line
        timeArray = new ArrayList<>();
        timeArray = getTimeLineHours();
        TimeLineAdapter timeAdapter = new TimeLineAdapter(getContext(), timeArray);

        final LinearLayoutManager timeLayoutManager = new LinearLayoutManager(_ctx, LinearLayoutManager.VERTICAL, false);
        timelineRecyclerView.setLayoutManager(timeLayoutManager);
        timelineRecyclerView.setNestedScrollingEnabled(false);
        timelineRecyclerView.setAdapter(timeAdapter);
        timelineRecyclerView.setHasFixedSize(true);
    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try {
            // try to load provided date format, and fallback to default otherwise
            String dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        } finally {
            ta.recycle();
        }
    }

    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        header = (LinearLayout) findViewById(R.id.calendar_header);
        btnPrev = (ImageView) findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView) findViewById(R.id.calendar_next_button);
        txtDate = (TfTextView) findViewById(R.id.calendar_date_display);
        tv_remark = (TfTextView) findViewById(R.id.tv_remark);
        grid = (FamiliarRecyclerView) findViewById(R.id.calendar_grid);

        timelineRecyclerView = (RecyclerView) findViewById(R.id.timelineRecyclerView);
        timelineLayout = (LinearLayout) findViewById(R.id.timelineLayout);
        linearCalenderViewHeader = (LinearLayout) findViewById(R.id.linearCalenderViewHeader);
        ll_bottom_layout = (LinearLayout) findViewById(R.id.ll_bottom_layout);
        calendarScrollView = (CalendarScrollView) findViewById(R.id.scrollView);

        //blankView = findViewById(R.id.blankView);

        btnDay = (TfButton) findViewById(R.id.btnDay);
        btnWeek = (TfButton) findViewById(R.id.btnWeek);
        btnMonth = (TfButton) findViewById(R.id.btnMonth);

        btnMapping = (TfButton) findViewById(R.id.btnMapping);
        btnRecruitment = (TfButton) findViewById(R.id.btnRecruitment);
        btnDeleteAll = (TfButton) findViewById(R.id.btnDeleteAll);
    }

    private void assignClickHandlers() {

        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dayPlans = new ArrayList<Plan>();
                switch (mode) {

                    //region DAY_MODE

                    case DAY:
                        Calendar tempCal = currentDate;
                        int curMonth = tempCal.get(Calendar.MONTH) + 1;

                        Log.e("current_month", curMonth + "#!");

                        currentDate.add(Calendar.DAY_OF_MONTH, 1);
                        fetchRemarkForBottomLayout();
                        int nextMonth = currentDate.get(Calendar.MONTH) + 1;

                        Log.e("next_month", nextMonth + "#!");

                        if (nextMonth > curMonth) {
                            if (onCalendarChangeListener != null) {
                                onCalendarChangeListener.onMonthChange();
                            }
                            isMonthChange = true;

                        } else {
                            isMonthChange = false;
                            String d = ConstantFormats.ymdFormat.format(currentDate.getTime());
                            try {
                                for (int i = 0; i < monthPlans.size(); i++) {
                                    DatePlan datePlan = monthPlans.get(i);
                                    if (datePlan != null && datePlan.getDate().equals(d)) {
                                        Log.e("select", datePlan.getDate() + " -- " + datePlan.getPlan().size());
                                        setDayPlan(datePlan.getPlan());
                                        break;
                                    } else {
                                        setDayPlan(new ArrayList<Plan>());
                                    }
                                }

                                notifyAdapter();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        dayAdapter.notifyDataSetChanged();
                        break;
                    //endregion

                    //region MONTH_MODE

                    case MONTH:
                        currentDate.add(Calendar.MONTH, 1);
                        if (onCalendarChangeListener != null) {
                            onCalendarChangeListener.onMonthChange();
                        }
                        break;
                    //endregion
                }

                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dayPlans = new ArrayList<Plan>();

                switch (mode) {

                    //region DAY_MODE

                    case DAY:

                        Calendar tempCal = currentDate;
                        int curMonth = tempCal.get(Calendar.MONTH) + 1;

                        Log.e("current_month", curMonth + "#!");

                        currentDate.add(Calendar.DAY_OF_MONTH, -1);
                        fetchRemarkForBottomLayout();
                        int nextMonth = currentDate.get(Calendar.MONTH) + 1;

                        Log.e("next_month", nextMonth + "#!");

                        if (nextMonth < curMonth) {
                            if (onCalendarChangeListener != null) {
                                onCalendarChangeListener.onMonthChange();
                            }
                            isMonthChange = true;

                        } else {

                            isMonthChange = false;

                            String d = ConstantFormats.ymdFormat.format(currentDate.getTime());

                            try {
                                for (int i = 0; i < monthPlans.size(); i++) {
                                    DatePlan datePlan = monthPlans.get(i);

                                    if (datePlan != null && datePlan.getDate().equals(d)) {
                                        Log.e("select", datePlan.getDate() + " -- " + datePlan.getPlan().size());
                                        setDayPlan(datePlan.getPlan());
                                        break;
                                    } else {
                                        setDayPlan(new ArrayList<Plan>());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    //endregion

                    //region MONTH_MODE

                    case MONTH:
                        currentDate.add(Calendar.MONTH, -1);
                        if (onCalendarChangeListener != null) {
                            onCalendarChangeListener.onMonthChange();
                        }
                        break;
                    //endregion

                }

                updateCalendar();
            }
        });

        btnDay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDay.setBackgroundResource(R.drawable.selected_left_shape);
                btnWeek.setBackgroundResource(R.color.un_selected);
                btnMonth.setBackgroundResource(R.drawable.unselected_right_shape);
                setMode(MODE.DAY);
                updateCalendar();
            }
        });

        btnWeek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDay.setBackgroundResource(R.drawable.unselected_left_shape);
                btnWeek.setBackgroundResource(R.color.selected);
                btnMonth.setBackgroundResource(R.drawable.unselected_right_shape);
                setMode(MODE.WEEK);
                updateCalendar();
            }
        });

        btnMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                linearCalenderViewHeader.setVisibility(GONE);
                btnMonth.setBackgroundResource(R.drawable.selected_right_shape);
                btnWeek.setBackgroundResource(R.color.un_selected);
                btnDay.setBackgroundResource(R.drawable.unselected_left_shape);
                setMode(MODE.MONTH);

                if (onViewChangeListener != null) {
                    onViewChangeListener.onViewChange();
                }
            }
        });

        btnMapping.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCalendarActionClickListener.onCalendarActionCalled(CalendarOptions.MAPPPING.ordinal());
            }
        });

        btnRecruitment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCalendarActionClickListener.onCalendarActionCalled(CalendarOptions.RECRUITMENT.ordinal());
            }
        });

        btnDeleteAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dayPlans.clear();
                dayAdapter.notifyDataSetChanged();
                onCalendarActionClickListener.onCalendarActionCalled(CalendarOptions.DELETEALL.ordinal());
            }
        });
    }

    /**
     * Display dates correctly in grid week/month/day wise
     */
    public void updateCalendar() {

        switch (mode) {

            //region DAY_MODE

            case DAY:

                String d = ConstantFormats.ymdFormat.format(currentDate.getTime());

                if (monthPlans != null && monthPlans.size() > 0) {
                    for (int i = 0; i < monthPlans.size(); i++) {
                        DatePlan datePlan = monthPlans.get(i);
                        if (datePlan.getDate().equals(d)) {
                            Log.e("select", datePlan.getDate() + " -- " + datePlan.getPlan().size());
                            setDayPlan(datePlan.getPlan());
                            // grid.setAdapter(null);
                            break;
                        }
                    }
                } else {

                }


                header.setVisibility(View.GONE);
                linearCalenderViewHeader.setVisibility(VISIBLE);
                //blankView.setVisibility(View.VISIBLE);
                timelineLayout.setVisibility(View.VISIBLE);

                // update title
                txtDate.setText(ConstantFormats.sdf_day.format(currentDate.getTime()));

                // dayAdapter.setDayPlan(dayPlans, currentDate);
                grid.setLayoutManager(linearLayoutManager);
                grid.setNestedScrollingEnabled(false);
                try {
                    grid.setAdapter(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                grid.setAdapter(dayAdapter);
                grid.setHasFixedSize(true);
                //   dayAdapter.notifyDataSetChanged();

                break;
            //endregion

            //region WEEK_MODE
            case WEEK:
                timelineLayout.setVisibility(VISIBLE);
                header.setVisibility(View.GONE);
                grid.setVisibility(View.VISIBLE);
                linearCalenderViewHeader.setVisibility(VISIBLE);
                break;
            //endregion

            //region MONTH_MODE

            case MONTH:

                // In month mode we need header for displaying Weekday Names
                // Also we need to diaply grid for displaying days.

                dayPlans = new ArrayList<>();
                header.setVisibility(View.VISIBLE);
                linearCalenderViewHeader.setVisibility(View.GONE);
                timelineLayout.setVisibility(View.GONE);
                //blankView.setVisibility(View.GONE);

                ArrayList<Date> cells = getMonthDates();
                // For month we set 7 columns for displaying 7 days in a row
                GridLayoutManager manager = new GridLayoutManager(_ctx, 7);
                grid.setLayoutManager(manager);
                grid.setOnItemClickListener(null);

                monthAdapter = new MonthAdapter(getContext(), cells, currentDate);

                monthAdapter.setCurrentDate(currentDate);
                monthAdapter.setOnDateSelectListener(new MonthAdapter.onDateSelectListener() {
                    @Override
                    public void onDateSelect(Calendar currentCalendar) {
                        currentDate = currentCalendar;
                        timelineLayout.setVisibility(VISIBLE);
                        setMode(MODE.DAY);

                        btnDay.setBackgroundResource(R.drawable.selected_left_shape);
                        btnMonth.setBackgroundResource(R.drawable.unselected_right_shape);

                        setDay();

                        if (onGridSelectListener != null) {
                            onGridSelectListener.onGridSelect(currentCalendar);
                        }
                    }
                });

                try {
                    grid.setAdapter(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                grid.setAdapter(monthAdapter);
                grid.setHasFixedSize(true);
                monthAdapter.notifyDataSetChanged();

                // update title
                txtDate.setText(ConstantFormats.sdf_month.format(currentDate.getTime()));
                updateHeader();

                break;
            //endregion

        }

    }

    public MODE getMode() {
        return mode;
    }

    private void setDay() {

        String d = ConstantFormats.ymdFormat.format(currentDate.getTime());

        if (monthPlans != null && monthPlans.size() > 0)
            for (int i = 0; i < monthPlans.size(); i++) {
                DatePlan datePlan = monthPlans.get(i);

                if (datePlan != null && datePlan.getDate().equals(d)) {
                    Log.e("select", datePlan.getDate() + " -- " + datePlan.getPlan().size());
                    setDayPlan(datePlan.getPlan());
                    break;
                } else {
                    dayPlans = new ArrayList<Plan>();
                }
            }
    }

    private ArrayList<TimeLineHour> getTimeLineHours() {

        // temp set calender...
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.JANUARY, 1, 8, 0, 0);

        // initial array to hold 24
        ArrayList<TimeLineHour> hours = new ArrayList<>();

        // All Day
        TimeLineHour th = new TimeLineHour();
        th.setTime("All\nDay");
        th.setFormat("");
        hours.add(th);

        for (int i = 0; i < 24; i++) {
            Date date = calendar.getTime();

            TimeLineHour hour = new TimeLineHour();
            hour.setTime(ConstantFormats.hourMinuteFormat.format(date));
            hour.setFormat(ConstantFormats.ampmFormat.format(date));

            hours.add(hour);
            calendar.add(Calendar.MINUTE, 30);
        }

        return hours;

    }

    private ArrayList<TimeLineHour> getTimeLineHoursSimple() {

        // temp set calender...
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.JANUARY, 1, 8, 0, 0);

        // initial array to hold 24
        ArrayList<TimeLineHour> hours = new ArrayList<>();

        // All Day
        TimeLineHour th = new TimeLineHour();
        th.setTime("All\nDay");
        th.setFormat("");
        hours.add(th);

        for (int i = 0; i < 24; i++) {

            Date date = calendar.getTime();

            TimeLineHour hour = new TimeLineHour();
            hour.setTime(ConstantFormats.hourMinuteSecFormat.format(date));
            hour.setFormat("");

            hours.add(hour);
            calendar.add(Calendar.MINUTE, 30);
        }

        return hours;

    }


    private ArrayList<Date> getWeekDates() {

        ArrayList<Date> dates = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();
        calendar.add(Calendar.DAY_OF_MONTH, -calendar.get(Calendar.DAY_OF_WEEK) + 1); // for display SUN first.
        while (dates.size() < DAYS_COUNT_WEEK) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    private ArrayList<Date> getMonthDates() {

        ArrayList<Date> dates = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();
        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);
        // fill cells
        while (dates.size() < DAYS_COUNT) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    private void updateHeader() {
        // set header color according to current season
        int month = currentDate.get(Calendar.MONTH);
        int season = monthSeason[month];
        int color = rainbow[season];
        header.setBackgroundColor(getResources().getColor(color));
    }

    public interface onViewChangeListener {
        void onViewChange();
    }

    public interface onMonthChangeListener {
        void onMonthChange();
    }

    public interface onGridSelectListener {
        void onGridSelect(Calendar calendar);
    }

    public enum CalendarOptions {
        MAPPPING, RECRUITMENT, DELETEALL;
    }

    public interface OnCalendarActionClickListener {
        void onCalendarActionCalled(int optionType);
    }

    public interface OnModeChangeListener {
        void onModeChange(MODE modeType);
    }
}
