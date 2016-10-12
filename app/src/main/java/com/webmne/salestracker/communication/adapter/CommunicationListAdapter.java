package com.webmne.salestracker.communication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.communication.CommunicationModel;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public class CommunicationListAdapter extends RecyclerView.Adapter<CommunicationListAdapter.CommunicationViewHolder> {

    private Context context;
    private ArrayList<CommunicationModel> communicationList;
    private onClickListener onClickListener;

    public CommunicationListAdapter(Context context, ArrayList<CommunicationModel> communicationList, onClickListener onClickListener) {
        this.context = context;
        this.communicationList = communicationList;
        this.onClickListener = onClickListener;
    }

    public void setEmployeeList(ArrayList<CommunicationModel> communicationList) {
        this.communicationList = new ArrayList<>();
        this.communicationList = communicationList;
        notifyDataSetChanged();
    }

    @Override
    public CommunicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_communication_list, parent, false);
        return new CommunicationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommunicationViewHolder holder, int position) {
        CommunicationModel communicationModel = communicationList.get(position);
        holder.setCommunicationDetail(communicationModel);
    }

    @Override
    public int getItemCount() {
        return communicationList.size();
    }

    class CommunicationViewHolder extends RecyclerView.ViewHolder {

        TfTextView txtTitle,txtDesc;
        ImageView imgDownload;
        LinearLayout parentView;

        public CommunicationViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TfTextView) itemView.findViewById(R.id.txtTitle);
            txtDesc = (TfTextView) itemView.findViewById(R.id.txtDesc);
            imgDownload = (ImageView) itemView.findViewById(R.id.imgDownload);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);
        }

        public void setCommunicationDetail(final CommunicationModel model) {

            txtTitle.setText(model.getTitle());
            txtDesc.setText(model.getDesc());

            imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onClickListener.onClick();

                }
            });

        }
    }

    public interface onClickListener {
        void onClick();
    }
}
