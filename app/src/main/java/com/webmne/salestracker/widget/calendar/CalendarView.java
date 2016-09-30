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
import android.widget.TextView;

import com.webmne.salestracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;


/**
 * Created by a7med on 28/06/2015.
 */
public class CalendarView extends LinearLayout {
    // for logging
    private static final String LOGTAG = "Calendar View";

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // how many days to show, defaults to 1 week, 7 days
    private static final int DAYS_COUNT_WEEK = 7;


    // default date format
    private static final String DATE_FORMAT = "dd-MMM yyyy";

    // date format
    private String dateFormat;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    //event handling
    private EventHandler eventHandler = null;

    private Context _ctx;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private RecyclerView grid, timelineRecyclerView;

    // seasons' rainbow
    int[] rainbow = new int[]{
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    // month-season association (northern hemisphere, sorry australia :)
    int[] monthSeason = new int[]{2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

    public static enum MODE {
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

    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
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
        txtDate = (TextView) findViewById(R.id.calendar_date_display);
        grid = (RecyclerView) findViewById(R.id.calendar_grid);
        timelineRecyclerView = (RecyclerView) findViewById(R.id.timelineRecyclerView);
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
                    case WEEK:
                        currentDate.add(Calendar.WEEK_OF_MONTH, 1);
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
                    case WEEK:
                        currentDate.add(Calendar.WEEK_OF_MONTH, -1);
                        break;
                    case MONTH:
                        currentDate.add(Calendar.MONTH, -1);
                        break;

                }
                updateCalendar(events);

            }
        });

//        // long-pressing a day
//        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id) {
//                // handle long-press
//                if (eventHandler == null)
//                    return false;
//
//                eventHandler.onDayLongPress((Date) view.getItemAtPosition(position));
//                return true;
//            }
//        });
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar() {
        updateCalendar(null);
    }

    /**
     * Display dates correctly in grid week/month/day wise
     */
    public void updateCalendar(HashSet<Date> evnts) {

        this.events = evnts;

        switch (mode) {

            case DAY:

                header.setVisibility(GONE);
                grid.setVisibility(GONE);
                timelineRecyclerView.setVisibility(VISIBLE);

                // update title
                SimpleDateFormat sdf_day = new SimpleDateFormat("EEE dd-MMM-yyyy");
                txtDate.setText(sdf_day.format(currentDate.getTime()));
                break;

            case WEEK:

                // get time line
                ArrayList<String> timeArray = getTimeLineHours();
                TimeLineAdapter timeAdapter = new TimeLineAdapter(getContext(), timeArray);

                LinearLayoutManager timeLayoutManager = new LinearLayoutManager(_ctx, LinearLayoutManager.VERTICAL, false);
                timelineRecyclerView.setLayoutManager(timeLayoutManager);
                timelineRecyclerView.setNestedScrollingEnabled(true);
                timelineRecyclerView.setAdapter(timeAdapter);

                header.setVisibility(GONE);
                timelineRecyclerView.setVisibility(VISIBLE);

                if (!grid.isShown()) {
                    grid.setVisibility(VISIBLE);
                }

                ArrayList<Date> cells_week = getWeekDates();
                cells_week.add(0, new Date());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_ctx, LinearLayoutManager.HORIZONTAL, false);
                grid.setLayoutManager(linearLayoutManager);
                WeekAdapter weekAdapter = new WeekAdapter(getContext(), cells_week, events);

                grid.addItemDecoration(new DividerItemDecoration(_ctx, DividerItemDecoration.HORIZONTAL_LIST));
                grid.setNestedScrollingEnabled(true);
                grid.setAdapter(weekAdapter);
                weekAdapter.notifyDataSetChanged();

                // update title
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");

                Date firstDate = cells_week.get(0);
                Date lastDate = cells_week.get(cells_week.size() - 1);
                txtDate.setText(String.format("%s  ~  %s", sdf.format(firstDate.getTime()), sdf.format(lastDate.getTime())));
                updateHeader();

                break;

            case MONTH:

                // In month mode we need header for displaying Weekday Names
                // Also we need to diaply grid for displaying days.
                header.setVisibility(VISIBLE);
                timelineRecyclerView.setVisibility(GONE);

                if (!grid.isShown())
                    grid.setVisibility(VISIBLE);

                ArrayList<Date> cells = getMonthDates();
                // For month we set 7 columns for displaying 7 days in a row
                GridLayoutManager manager = new GridLayoutManager(_ctx, 7);
                grid.setLayoutManager(manager);
                MonthAdapter monthAdapter = new MonthAdapter(getContext(), cells, events);
                monthAdapter.setCurrentDate(currentDate);
                grid.removeItemDecoration(dividerItemDecoration);
                grid.setAdapter(monthAdapter);

                monthAdapter.notifyDataSetChanged();

                // update title
                SimpleDateFormat sdf_month = new SimpleDateFormat(dateFormat);
                txtDate.setText(sdf_month.format(currentDate.getTime()));
                updateHeader();

                break;
        }


    }

    private ArrayList<String> getTimeLineHours() {

        // temp set calender...
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.JANUARY, 1, 0, 0, 0);

        // initial array to hold 24
        ArrayList<String> hours = new ArrayList<>();
        hours.add("\nTime\n");
        for (int i = 0; i < 24; i++) {

            Date date = calendar.getTime();
            SimpleDateFormat sdf_day = new SimpleDateFormat("hh a");
            hours.add("\n" + sdf_day.format(date) + "\n");
            calendar.add(Calendar.HOUR_OF_DAY, 1);

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

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler {
        void onDayLongPress(Date date);
    }


}
