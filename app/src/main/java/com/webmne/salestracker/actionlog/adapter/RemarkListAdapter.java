package com.webmne.salestracker.actionlog.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.model.RemarkModel;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 22-08-2016.
 */
public class RemarkListAdapter extends RecyclerView.Adapter<RemarkListAdapter.LogHolder> {

    private Context context;
    private ArrayList<RemarkModel> remarkModelList;

    public RemarkListAdapter(Context context, ArrayList<RemarkModel> remarkModelList) {
        this.context = context;
        this.remarkModelList = remarkModelList;
    }

    public void setRemarkList(ArrayList<RemarkModel> remarkModelList) {
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
        RemarkModel model = remarkModelList.get(position);
        holder.setActionLog(model);
    }

    @Override
    public int getItemCount() {
        return remarkModelList.size();
    }

    class LogHolder extends RecyclerView.ViewHolder {

        TfTextView txtName,txtDesc,txtDate,txtPosition;

        public LogHolder(View itemView) {
            super(itemView);
            txtName = (TfTextView) itemView.findViewById(R.id.txtName);
            txtPosition = (TfTextView) itemView.findViewById(R.id.txtPosition);
            txtDesc = (TfTextView) itemView.findViewById(R.id.txtDesc);
            txtDate = (TfTextView) itemView.findViewById(R.id.txtDate);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void setActionLog(RemarkModel model) {

            txtName.setText(model.getName());
            txtPosition.setText(String.format("(%s)", model.getPosition()));
            txtDesc.setText(model.getDetail());
            txtDate.setText(model.getDate());

//            String text=null;
//            txtAgentName.setText(String.format("%s", text));


        }
    }
}
