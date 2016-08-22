package com.webmne.salestracker.contacts.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;

import com.webmne.salestracker.R;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vatsaldesai on 19-08-2016.
 */
public class ContactPageAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    Context con;

    public ContactPageAdapter(FragmentManager fm, Context con) {
        super(fm);
        this.con = con;
    }

    public void addFragment(Fragment fragment, String title)
    {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    public View getTabView(int position)
    {
        TfTextView tfTextView = new TfTextView(con);
        tfTextView.setText(fragmentTitleList.get(position));
        tfTextView.setTextColor(ContextCompat.getColor(con, R.color.white));
        tfTextView.setGravity(Gravity.CENTER);
        return tfTextView;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return fragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
