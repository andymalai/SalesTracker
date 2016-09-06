package com.webmne.salestracker.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 15-08-2016.
 */
public class BranchAdapter extends ArrayAdapter<Branch> {

    private ArrayList<Branch> branchModels;
    private Context context;
    private int textViewResourceId;
    private LayoutInflater inflater;

    public BranchAdapter(Context context, int textViewResourceId, ArrayList<Branch> branchModels) {
        super(context, textViewResourceId);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.branchModels = branchModels;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return branchModels.size();
    }

    @Override
    public Branch getItem(int position) {
        return branchModels.get(position);
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
        txtItem.setText(branchModels.get(position).getBranchName());

        return convertView;
    }

}
