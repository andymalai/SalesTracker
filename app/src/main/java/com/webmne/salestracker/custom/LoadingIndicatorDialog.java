package com.webmne.salestracker.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.victor.loading.rotate.RotateLoading;
import com.webmne.salestracker.R;

/**
 * Created by sagartahelyani on 05-09-2016.
 */
public class LoadingIndicatorDialog extends Dialog {

    private String msg;
    private Context context;
    private RotateLoading rotateLoading;

    public LoadingIndicatorDialog(Context context, String msg, int themeResId) {
        super(context, themeResId);
        this.context = context;
        this.msg = msg;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loading, null);
        TextView txtLoadingText = (TextView) view.findViewById(R.id.txtLoadingText);
        txtLoadingText.setText(msg);

        rotateLoading = (RotateLoading) view.findViewById(R.id.rotateLoading);
        rotateLoading.start();

        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        rotateLoading.stop();
    }
}
