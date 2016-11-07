package com.webmne.salestracker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by vatsaldesai on 28-10-2016.
 */

public class CalendarScrollView extends ScrollView {

    public interface OnScrollChangedListener {
        void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public CalendarScrollView(Context context) {
        super(context);
    }

    public CalendarScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
