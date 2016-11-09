package com.webmne.salestracker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.webmne.salestracker.R;
import com.webmne.salestracker.helper.Functions;

/**
 * Created by dhruvil on 27-07-2016.
 */

public class TfDropdownEditText extends EditText {

    private Context _ctx;

    public TfDropdownEditText(Context context) {
        super(context);
        if (!isInEditMode()) {
            this._ctx = context;
            init();
        }
    }

    public TfDropdownEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            this._ctx = context;
            init();
        }
    }

    private void init() {
        try {
            setTypeface(Functions.getRegularFont(_ctx));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
