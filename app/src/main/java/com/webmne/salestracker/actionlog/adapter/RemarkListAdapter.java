package com.webmne.salestracker.actionlog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Remark;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 22-08-2016.
 */
public class RemarkListAdapter extends RecyclerView.Adapter<RemarkListAdapter.LogHolder> {

    private Context context;
    private ArrayList<Remark> remarkModelList;

    public RemarkListAdapter(Context context, ArrayList<Remark> remarkModelList) {
        this.context = context;
        this.remarkModelList = remarkModelList;
    }

    public void setRemarkList(ArrayList<Remark> remarkModelList) {
        this.remarkModelList = new ArrayList<>();
        this.remarkModelList = remarkModelList;
        notifyDataSetChanged();
    }

    @Override
    public LogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_row_remark_list, parent, false);

        return new LogHolder(view);
    }

    @Override
    public void onBindViewHolder(LogHolder holder, int position) {
        Remark model = remarkModelList.get(position);
        holder.setRemark(model);
    }

    @Override
    public int getItemCount() {
        return remarkModelList.size();
    }

    class LogHolder extends RecyclerView.ViewHolder {

        TfTextView txtName, txtDesc, txtDate, txtPosition;

        public LogHolder(View itemView) {
            super(itemView);
            txtName = (TfTextView) itemView.findViewById(R.id.txtName);
            txtPosition = (TfTextView) itemView.findViewById(R.id.txtPosition);
            txtDesc = (TfTextView) itemView.findViewById(R.id.txtDesc);
            txtDate = (TfTextView) itemView.findViewById(R.id.txtDate);
        }

        public void setRemark(Remark model) {
            txtName.setText(model.getName());
            txtPosition.setText(String.format("(%s)", model.getPosition()));
            txtDesc.setText(model.getDescription());
            txtDate.setText("Date");
        }
    }
}
