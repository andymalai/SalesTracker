package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.custom.WhiteLineDividerItemDecoration;
import com.webmne.salestracker.helper.ConstantFormats;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;


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

    private onGridSelectListener onGridSelectListener;

    public void setOnGridSelectListener(CalendarView.onGridSelectListener onGridSelectListener) {
        this.onGridSelectListener = onGridSelectListener;
    }


    // default date format
    private static final String DATE_FORMAT = "dd-MMM yyyy";

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    private Context _ctx;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TfTextView txtDate;
    private RecyclerView grid, timelineRecyclerView;
    private LinearLayout timelineLayout;
    private View blankView;
    private ArrayList<TimeLineHour> timeArray;

    // seasons' rainbow
    int[] rainbow = new int[]{
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    // month-season association (northern hemisphere, sorry australia :)
    int[] monthSeason = new int[]{2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

    public String getCurrentDate() {
        return ConstantFormats.sdf_day.format(currentDate.getTime());
    }

    public enum MODE {
        DAY,
        WEEK,
        MONTH
    }

    // This is default mode to set as month
    private MODE mode = MODE.MONTH;

    // Events names to be passed for displaying particular events for a specific dates
    private HashSet<Date> events = null;

    //This item decoration is only for week view
    private DividerItemDecoration dividerItemDecoration;

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
        updateCalendar(events);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs) {

        _ctx = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);

        dividerItemDecoration = new DividerItemDecoration(_ctx, DividerItemDecoration.VERTICAL_LIST);
        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();
        assignTimelineAdapter();

    }

    private void assignTimelineAdapter() {
        // get time line
        timeArray = new ArrayList<>();
        timeArray = getTimeLineHours();
        TimeLineAdapter timeAdapter = new TimeLineAdapter(getContext(), timeArray);

        final LinearLayoutManager timeLayoutManager = new LinearLayoutManager(_ctx, LinearLayoutManager.VERTICAL, false);
        timelineRecyclerView.setLayoutManager(timeLayoutManager);
        timelineRecyclerView.addItemDecoration(new WhiteLineDividerItemDecoration(_ctx));
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
        grid = (RecyclerView) findViewById(R.id.calendar_grid);
        timelineRecyclerView = (RecyclerView) findViewById(R.id.timelineRecyclerView);
        timelineLayout = (LinearLayout) findViewById(R.id.timelineLayout);
        blankView = findViewById(R.id.blankView);
    }

    private void assignClickHandlers() {

        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case DAY:
                        currentDate.add(Calendar.DAY_OF_YEAR, 1);
                        break;
                    case MONTH:
                        currentDate.add(Calendar.MONTH, 1);
                        break;
                }
                updateCalendar(events);
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (mode) {
                    case DAY:
                        currentDate.add(Calendar.DAY_OF_YEAR, -1);
                        break;
                    case MONTH:
                        currentDate.add(Calendar.MONTH, -1);
                        break;

                }
                updateCalendar(events);
            }
        });
    }

    /**
     * Display dates correctly in grid week/month/day wise
     */
    public void updateCalendar(HashSet<Date> evnts) {

        this.events = evnts;

        switch (mode) {
            case DAY:

                header.setVisibility(View.GONE);
                blankView.setVisibility(View.VISIBLE);
                timelineLayout.setVisibility(View.VISIBLE);

                // update title
                txtDate.setText(ConstantFormats.sdf_day.format(currentDate.getTime()));

                DayPlanAdapter adapter = new DayPlanAdapter(getEvents());
                grid.setLayoutManager(linearLayoutManager);
                grid.setNestedScrollingEnabled(false);
                grid.setAdapter(adapter);
                grid.setHasFixedSize(true);

                break;

            case MONTH:

                // In month mode we need header for displaying Weekday Names
                // Also we need to diaply grid for displaying days.

                header.setVisibility(View.VISIBLE);
                timelineLayout.setVisibility(View.GONE);
                blankView.setVisibility(View.GONE);

                ArrayList<Date> cells = getMonthDates();
                // For month we set 7 columns for displaying 7 days in a row
                GridLayoutManager manager = new GridLayoutManager(_ctx, 7);
                grid.setLayoutManager(manager);

                MonthAdapter monthAdapter = new MonthAdapter(getContext(), cells, events);
                monthAdapter.setCurrentDate(currentDate);
                monthAdapter.setOnDateSelectListener(new MonthAdapter.onDateSelectListener() {
                    @Override
                    public void onDateSelect(Calendar currentCalendar) {
                        currentDate = currentCalendar;
                        timelineLayout.setVisibility(VISIBLE);
                        setMode(MODE.DAY);
                        if (onGridSelectListener != null) {
                            onGridSelectListener.onGridSelect(currentCalendar);
                        }
                    }
                });

                grid.setAdapter(monthAdapter);
                grid.setHasFixedSize(true);
                monthAdapter.notifyDataSetChanged();

                // update title
                txtDate.setText(ConstantFormats.sdf_month.format(currentDate.getTime()));
                updateHeader();

                break;
        }

    }

    private ArrayList<Event> getEvents() {

        ArrayList<Event> events = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            Event event = new Event();
            event.setName("Name " + i);
            event.setRemark("Remark " + i);
            events.add(event);
        }
        return events;
    }

    public interface onGridSelectListener {
        void onGridSelect(Calendar calendar);
    }

    private ArrayList<TimeLineHour> getTimeLineHours() {

        // temp set calender...
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.JANUARY, 1, 0, 0, 0);

        // initial array to hold 24
        ArrayList<TimeLineHour> hours = new ArrayList<>();
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

}
