package com.webmne.salestracker.helper;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by sagartahelyani on 30-09-2016.
 */

public class ConstantFormats {

    public static final SimpleDateFormat hourMinuteFormat = new SimpleDateFormat("hh.mm", Locale.US);
    public static final SimpleDateFormat hourMinuteSecFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
    public static final SimpleDateFormat ampmFormat = new SimpleDateFormat("a", Locale.US);

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    public static final SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static final SimpleDateFormat sdf_day = new SimpleDateFormat("EEE dd-MMM-yyyy", Locale.US);
    public static final SimpleDateFormat sdf_month = new SimpleDateFormat("MMMM yyyy", Locale.US);

    public static final SimpleDateFormat zoneFormat = new SimpleDateFormat("Z", Locale.US);

    public static final SimpleDateFormat MMMFormat = new SimpleDateFormat("MMM", Locale.US);
    public static final SimpleDateFormat MMFormat = new SimpleDateFormat("MM", Locale.US);
    public static final SimpleDateFormat ddFormat = new SimpleDateFormat("dd", Locale.US);
    public static final SimpleDateFormat yyyyFormat = new SimpleDateFormat("yyyy", Locale.US);

}
