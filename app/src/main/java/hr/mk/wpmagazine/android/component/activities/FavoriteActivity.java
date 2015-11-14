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
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import butterknife.InjectView;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.ui.fragments.FavoriteFragment;

/**
 * Created by Mur0 on 3/30/2015.
 */
public class FavoriteActivity extends AbsBaseActivity implements ObservableScrollViewCallbacks {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.favorite_activity);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        init(savedInstanceState);
    }

    protected void init(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        setTint(getResources().getColor(R.color.colorPrimaryDark));
        toolbar.setTitle(getResources().getString(R.string.favorite));
        if (savedInstanceState == null) {
            final FavoriteFragment fragment = new FavoriteFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, fragment.getFragmentTag()).commit();
        }
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {

    }

    @Override
    public void onDownMotionEvent() {

    }

//    private void showToolbar() {
//        float headerTranslationY = toolbar.getTranslationY();
//        if (headerTranslationY != 0) {
//            toolbar.animate().cancel();
//            toolbar.animate().translationY(0).setDuration(400).start();
//        }
//    }
//
//    private void hideToolbar() {
//        float headerTranslationY = toolbar.getTranslationY();
//        int toolbarHeight = toolbar.getHeight();
//        if (headerTranslationY != -toolbarHeight) {
//            toolbar.animate().cancel();
//            toolbar.animate().translationY(-toolbarHeight).setDuration(400).start();
//        }
//    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        final ActionBar ab = getSupportActionBar();
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
