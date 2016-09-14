package com.webmne.salestracker.contacts;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
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
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        initPager();

        actionListener();

    }

    private void actionListener() {
        contactBinding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Fragment fragment = contactPageAdapter.getItem(contactBinding.viewpager.getCurrentItem());

                if (fragment instanceof BranchContactFragment) {

                    BranchContactFragment branchContactFragment = (BranchContactFragment) fragment;

                    branchContactFragment.searchBranchContact(query);
                }
                else
                {
                    DepartmentContactFragment departmentContactFragment = (DepartmentContactFragment) fragment;

                    departmentContactFragment.searchDepartmentContact(query);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic

                Fragment fragment = contactPageAdapter.getItem(contactBinding.viewpager.getCurrentItem());

                if (fragment instanceof BranchContactFragment) {

                    BranchContactFragment branchContactFragment = (BranchContactFragment) fragment;

                    branchContactFragment.searchBranchContact(newText);
                }
                else
                {
                    DepartmentContactFragment departmentContactFragment = (DepartmentContactFragment) fragment;

                    departmentContactFragment.searchDepartmentContact(newText);
                }

                return true;
            }
        });

        contactBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    contactBinding.searchView.setHint(getString(R.string.search_branch_contact));
                } else if (position == 1) {
                    contactBinding.searchView.setHint(getString(R.string.search_department_contact));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agent, menu);
        searchItem = menu.findItem(R.id.action_search);
        contactBinding.searchView.setMenuItem(searchItem);

        contactBinding.searchView.setHint(getString(R.string.search_branch_contact));
        contactBinding.searchView.setVoiceSearch(true);

        return true;
    }

}
