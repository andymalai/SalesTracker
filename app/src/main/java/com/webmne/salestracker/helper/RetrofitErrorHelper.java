package com.webmne.salestracker.helper;

import android.content.Context;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;

import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sagartahelyani on 13-09-2016.
 */
public class RetrofitErrorHelper {

    public static void showErrorMsg(Throwable throwable, Context context) {

        if (throwable instanceof TimeoutException) {
            SimpleToast.error(context, context.getString(R.string.time_out), context.getString(R.string.fa_error));

        } else if (throwable instanceof UnknownHostException) {
            SimpleToast.error(context, context.getString(R.string.no_internet_connection), context.getString(R.string.fa_error));

        } else {
            SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
        }
    }
}
