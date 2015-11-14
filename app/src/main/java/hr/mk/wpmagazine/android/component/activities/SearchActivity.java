/*
 *
 *  *
 *  *  * ****************************************************************************
 *  *  * Copyright (c) 2015. Muriel Kamgang Mabou
 *  *  * All rights reserved.
 *  *  *
 *  *  * This file is part of project AndroidWPTemplate.
 *  *  * It can not be copied and/or distributed without the
 *  *  * express permission of Muriel Kamgang Mabou
 *  *  * ****************************************************************************
 *  *
 *  *
 *
 */

package hr.mk.wpmagazine.android.component.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.ui.fragments.SearchFragment;

/**
 * Created by Mur0 on 3/31/2015.
 */
public class SearchActivity extends FavoriteActivity {

    private SearchFragment fragment;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (savedInstanceState == null) {
            fragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, fragment.getFragmentTag()).commit();

        } else {
            fragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) { // will move this to absActivity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        setTint(getResources().getColor(R.color.colorPrimaryDark));
        toolbar.setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                fragment.search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.isEmpty())
                    fragment.search(s);
                return false;
            }
        });
        searchView.setIconifiedByDefault(false);
        MenuItem menuItem = menu.findItem(R.id.action_search);// force that shit of searchView to expand ! thank you Google
        menuItem.expandActionView();

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_search:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
