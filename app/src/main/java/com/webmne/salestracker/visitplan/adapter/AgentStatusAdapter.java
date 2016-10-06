package com.webmne.salestracker.visitplan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Tier;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 15-08-2016.
 */
public class AgentStatusAdapter extends ArrayAdapter<String> {

    private ArrayList<String> tierModels;
    private Context context;
    private int textViewResourceId;
    private LayoutInflater inflater;
    private boolean canOpen = true;

    public AgentStatusAdapter(Context context, int textViewResourceId, ArrayList<String> tierModels) {
        super(context, textViewResourceId);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.tierModels = tierModels;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCanOpen(boolean canOpen) {
        this.canOpen = canOpen;
    }

    @Override
    public String getItem(int position) {
        return tierModels.get(position);
    }

    @Override
    public int getCount() {
        return tierModels.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (canOpen) {
            return getCustomView(position, parent);
        } else {
            return new View(context);
        }
    }

    private View getCustomView(int position, ViewGroup parent) {

        View convertView = inflater.inflate(textViewResourceId, parent, false);

        TfTextView txtItem = (TfTextView) convertView.findViewById(R.id.txtItem);
        txtItem.setText(tierModels.get(position));

        return convertView;
    }

}
