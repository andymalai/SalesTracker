package com.webmne.salestracker.contacts;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.webmne.salestracker.R;
import com.webmne.salestracker.contacts.adapter.ContactPageAdapter;
import com.webmne.salestracker.contacts.fragment.BranchContactFragment;
import com.webmne.salestracker.contacts.fragment.DepartmentContactFragment;
import com.webmne.salestracker.databinding.ActivityContactBinding;

public class ContactActivity extends AppCompatActivity {

    ContactPageAdapter contactPageAdapter;

    ActivityContactBinding contactBinding;

    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_contact);

        init();
    }

    private void init() {
        contactBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact);

        if (contactBinding.toolbarLayout.toolbar != null) {
            contactBinding.toolbarLayout.toolbar.setTitle("");
        }
        setSupportActionBar(contactBinding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.contact_title));

        contactBinding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initPager();

    }

    private void initPager() {

        contactPageAdapter = new ContactPageAdapter(getSupportFragmentManager(), this);

        contactPageAdapter.addFragment(BranchContactFragment.newInstance(), getString(R.string.branch_contact));
        contactPageAdapter.addFragment(DepartmentContactFragment.newInstance(), getString(R.string.department_contact));

        contactBinding.viewpager.setAdapter(contactPageAdapter);

        contactBinding.tablayout.setupWithViewPager(contactBinding.viewpager);

        for (int i = 0; i < contactBinding.tablayout.getTabCount(); i++) {
            TabLayout.Tab tab = contactBinding.tablayout.getTabAt(i);
            tab.setCustomView(contactPageAdapter.getTabView(i));
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (contactBinding.searchView.isSearchOpen()) {
            contactBinding.searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agent, menu);
        searchItem = menu.findItem(R.id.action_search);
        contactBinding.searchView.setMenuItem(searchItem);

        contactBinding.searchView.setVoiceSearch(true);
        contactBinding.searchView.setHint(getString(R.string.search_branch_contact));
        return true;
    }
}
