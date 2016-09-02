package com.webmne.salestracker.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.databinding.LayoutEmptyViewBinding;

/**
 * Created by sagartahelyani on 20-06-2016.
 */
public class EmptyLayout extends LinearLayout {

    private Context context;
    private LayoutInflater inflater;

    LayoutEmptyViewBinding emptyViewBinding;

    public EmptyLayout(Context context) {
        super(context);
        if (!isInEditMode()) {
            this.context = context;
            init();
        }
    }

    public EmptyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            this.context = context;
            init();
        }
    }

    private void init() {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        emptyViewBinding = DataBindingUtil.inflate(inflater, R.layout.layout_empty_view, this, true);
    }

    public void setContent(String text) {
        emptyViewBinding.emptyTextView.setText(text);
    }

    public void setContent(String text, int icon) {
        emptyViewBinding.emptyTextView.setText(text);
        emptyViewBinding.emptyImageView.setImageResource(icon);
    }
}
