package com.webmne.salestracker.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.webmne.salestracker.R;
import com.webmne.salestracker.helper.Functions;

/**
 * Created by dhruvil on 27-07-2016.
 */

public class TfEditText extends MaterialEditText {

    private Context _ctx;

    public TfEditText(Context context) {
        super(context);
        if (!isInEditMode()) {
            this._ctx = context;
            init();
        }
    }

    public TfEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            this._ctx = context;
            init();
        }
    }

    private void init() {
        try {
            setFloatingLabel(FLOATING_LABEL_HIGHLIGHT);
            setPrimaryColor(ContextCompat.getColor(_ctx, R.color.half_black));
            setAccentTypeface(Functions.getRegularFont(_ctx));
            setTypeface(Functions.getRegularFont(_ctx));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
