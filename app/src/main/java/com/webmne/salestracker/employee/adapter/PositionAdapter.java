package com.webmne.salestracker.employee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.webmne.salestracker.R;
import com.webmne.salestracker.employee.model.PositionModel;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 15-08-2016.
 */
public class PositionAdapter extends ArrayAdapter<PositionModel> {

    private ArrayList<PositionModel> positionModelList;
    private Context context;
    private int textViewResourceId;
    private LayoutInflater inflater;
    private boolean canOpen = true;

    public PositionAdapter(Context context, int textViewResourceId, ArrayList<PositionModel> positionModelList) {
        super(context, textViewResourceId);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.positionModelList = positionModelList;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCanOpen(boolean canOpen) {
        this.canOpen = canOpen;
    }

    @Override
    public PositionModel getItem(int position) {
        return positionModelList.get(position);
    }

    @Override
    public int getCount() {
        return positionModelList.size();
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
        txtItem.setText(positionModelList.get(position).getName());

        return convertView;
    }

}
