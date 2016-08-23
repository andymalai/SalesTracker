package com.webmne.salestracker.actionlog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.model.Department;
import com.webmne.salestracker.actionlog.model.InCharge;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 15-08-2016.
 */
public class InChargeAdapter extends ArrayAdapter<InCharge> {

    private ArrayList<InCharge> agentModels;
    private Context context;
    private int textViewResourceId;
    private LayoutInflater inflater;

    public InChargeAdapter(Context context, int textViewResourceId, ArrayList<InCharge> agentModels) {
        super(context, textViewResourceId);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.agentModels = agentModels;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return agentModels.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(textViewResourceId, parent, false);

        TfTextView txtItem = (TfTextView) convertView.findViewById(R.id.txtItem);
        txtItem.setText(agentModels.get(position).getInChargeName());

        return convertView;
    }

}
