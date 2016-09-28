package com.webmne.salestracker.ui.dashboard.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.ActionLogListActivity;
import com.webmne.salestracker.agent.AgentsListActivity;
import com.webmne.salestracker.contacts.ContactActivity;
import com.webmne.salestracker.employee.EmployeeListActivity;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.TileId;
import com.webmne.salestracker.ui.dashboard.model.HomeTileBean;
import com.webmne.salestracker.visitplan.SalesVisitPlanActivity;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.HomeViewHolder> {

    private ArrayList<HomeTileBean> tiles;
    private Context context;

    public DashboardAdapter(Context context, ArrayList<HomeTileBean> tiles) {
        this.tiles = tiles;
        this.context = context;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_tile, parent, false);
        return new HomeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        HomeTileBean item = tiles.get(position);
        holder.setDetails(item);
    }

    @Override
    public int getItemCount() {
        return tiles.size();
    }

    public void setTiles(ArrayList<HomeTileBean> tiles) {
        this.tiles = new ArrayList<>();
        this.tiles = tiles;
        notifyDataSetChanged();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {

        ImageView imgHomeTileIcon;
        TfTextView txtHomeTileName;

        public HomeViewHolder(View itemView) {
            super(itemView);
            imgHomeTileIcon = (ImageView) itemView.findViewById(R.id.imgHomeTileIcon);
            txtHomeTileName = (TfTextView) itemView.findViewById(R.id.txtHomeTileName);
        }

        public void setDetails(final HomeTileBean item) {
            imgHomeTileIcon.setImageResource(item.getTileIcon());
            // imgHomeTileIcon.setColorFilter(item.getContentColor(), PorterDuff.Mode.SRC_ATOP);
            txtHomeTileName.setText(item.getTileName());
            // txtHomeTileName.setTextColor(item.getContentColor());
            itemView.setBackgroundColor(item.getBackgroundColor());
            itemView.setId(item.getId());
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            itemView.setOnClickListener(tileClickListsner);
        }
    }

    View.OnClickListener tileClickListsner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int selectedId = v.getId();

            if (selectedId == TileId.AGENTS.getId()) {
                Functions.fireIntent(context, AgentsListActivity.class);

            } else if (selectedId == TileId.CONTACTS.getId()) {
                Functions.fireIntent(context, ContactActivity.class);

            } else if (selectedId == TileId.ACTION_LOG.getId()) {
                Functions.fireIntent(context, ActionLogListActivity.class);

            } else if (selectedId == TileId.SALES_VISIT_PLAN.getId()) {
                Functions.fireIntent(context, SalesVisitPlanActivity.class);

            } else if (selectedId == TileId.EMPLOYEE.getId()) {
                Functions.fireIntent(context, EmployeeListActivity.class);

            } else if (selectedId == TileId.MANAGE_VISIT_PLAN.getId()) {

            } else if (selectedId == TileId.TARGET.getId()) {

            }
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };
}
