package com.webmne.salestracker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // this is the scale between width & height, for square should be == 1
        int scale = 1;

        if (width > (int) (scale * height + 0.5)) {
            width = width;
        } else {
            height = (int) (width / 2);
        }

        super.onMeasure(MeasureSpec.makeMeasureSpec(width / 2, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((int) (height / 1.2), MeasureSpec.EXACTLY)
        );
    }
}