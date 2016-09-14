package com.webmne.salestracker.helper.volley;

import android.content.Context;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;

/**
 * Created by sagartahelyani on 13-09-2016.
 */
public class VolleyErrorHelper {

    public static void showErrorMsg(VolleyError error, Context context) {

        if (error instanceof TimeoutError) {
            SimpleToast.error(context, context.getString(R.string.time_out), context.getString(R.string.fa_error));

        } else {
            SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
        }
    }
}
