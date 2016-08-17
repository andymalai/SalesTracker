package com.webmne.salestracker.ui.dashboard.model;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.webmne.salestracker.R;
import com.webmne.salestracker.widget.SquareLayout;


public class HomeTile extends SquareLayout {

    private Context _ctx;
    private LayoutInflater inflater;
    private TextView txtView;
    private HomeTileBean homeTileBean;
    private ImageView imgHomeTileIcon;

    public HomeTile(Context context) {
        super(context);
        _ctx = context;
        init();
    }

    public HomeTile(Context context, AttributeSet attrs) {
        super(context, attrs);
        _ctx = context;
        init();
    }

    private void init() {
        inflater = (LayoutInflater) _ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_home_tile, this);
        txtView = (TextView) findViewById(R.id.txtHomeTileName);
        imgHomeTileIcon = (ImageView) findViewById(R.id.imgHomeTileIcon);
    }

    public HomeTileBean getTile() {
        return homeTileBean;
    }

    public void setupTile(HomeTileBean bean) {
        this.homeTileBean = bean;
        txtView.setText(bean.getTileName());
        //setBackgroundColor(Color.parseColor(bean.getBackgroundColor()));
        imgHomeTileIcon.setImageResource(bean.getTileIcon());
    }

}
