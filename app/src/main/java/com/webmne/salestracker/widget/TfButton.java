package com.webmne.salestracker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.webmne.salestracker.helper.Functions;

/**
 * Created by dhruvil on 27-07-2016.
 */

public class TfButton extends Button {

    private Context _ctx;

    public TfButton(Context context) {
        super(context);
        if (!isInEditMode()) {
            this._ctx = context;
            init();
        }
    }

    public TfButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            this._ctx = context;
            init();
        }
    }

    private void init() {
        try {
            // setTypeface(Functions.getLatoFont(_ctx));
            setTypeface(Functions.getBoldFont(_ctx));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
