package com.webmne.salestracker.helper;

/**
 * @author jatin
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.UserProfile;
import com.webmne.salestracker.ui.LoginActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Functions {

    public static final String ServerDateTimeFormat = "yyyy/MM/dd HH:mm:ss";

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static void fireIntent(Context context, Class cls) {
        Intent i = new Intent(context, cls);
        context.startActivity(i);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static void fireIntent(Context context, Intent intent) {
        context.startActivity(intent);

    }

    public static void hideKeyPad(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static Typeface getRegularFont(Context _context) {
        Typeface tf = Typeface.createFromAsset(_context.getAssets(), "fonts/Ubuntu-Regular.ttf");
        return tf;
    }

    public static Typeface getBoldFont(Context _context) {
        Typeface tf = Typeface.createFromAsset(_context.getAssets(), "fonts/Ubuntu-Bold.ttf");
        return tf;
    }

    public static boolean emailValidation(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String toStr(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static int toLength(EditText editText) {
        return editText.getText().toString().trim().length();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static void openInMap(Context context, double latitude, double longitude, String labelName) {
        String newUri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + labelName + ")";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newUri));
        context.startActivity(intent);
    }

    public static void makePhoneCall(Context context, String callNo) {
        Intent dialIntent = new Intent();
        dialIntent.setAction(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + callNo));
        context.startActivity(dialIntent);
    }

    public static void sendMailTo(Context context, String emailId) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.msg));
        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    public static int getRandomColor(Context context) {
        Random r = new Random();
        int i1 = r.nextInt(4 - 1 + 1) + 1;

        if (i1 == 1) {
            return ContextCompat.getColor(context, R.color.color1);
        } else if (i1 == 2) {
            return ContextCompat.getColor(context, R.color.color2);
        } else if (i1 == 3) {
            return ContextCompat.getColor(context, R.color.color3);
        } else {
            return ContextCompat.getColor(context, R.color.color4);
        }
    }

    public static void showPrompt(Context context, String positiveText, String negativeText, String content, final onPromptListener onPromptListener) {
        new MaterialDialog.Builder(context)
                .content(content)
                .typeface(getBoldFont(context), getRegularFont(context))
                .positiveText(positiveText)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (onPromptListener != null) {
                            onPromptListener.onClickYes(dialog);
                        }
                    }
                })
                .negativeText(negativeText)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (onPromptListener != null) {
                            onPromptListener.onClickNo(dialog);
                        }
                    }
                }).show();
    }

    public static String parseDate(String inputDate, String outputPattern) {

        SimpleDateFormat inputFormat = new SimpleDateFormat(ServerDateTimeFormat);

        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(inputDate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getStatus(Context context, String status) {

        if (TextUtils.isEmpty(status)) {
            return "";

        } else if (status.equals(AppConstants.PENDING)) {
            return context.getString(R.string.pending);

        } else if (status.equals(AppConstants.REJECTED)) {
            return context.getString(R.string.reject);

        } else if (status.equals(AppConstants.PROCESSING)) {
            return context.getString(R.string.processing);

        } else if (status.equals(AppConstants.COMPLETE)) {
            return context.getString(R.string.completed);

        } else {
            return context.getString(R.string.pending);
        }

    }



    public interface onPromptListener {
        void onClickYes(MaterialDialog dialog);

        void onClickNo(MaterialDialog dialog);
    }

    public static void closeSession(Context context) {

        UserProfile userProfile = new UserProfile();
        PrefUtils.clearUserProfile(context, userProfile);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}
