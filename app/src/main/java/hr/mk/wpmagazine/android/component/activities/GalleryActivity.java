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
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import butterknife.InjectView;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.ui.fragments.GalleryDrawerFragment;
import hr.mk.wpmagazine.android.component.ui.fragments.GalleryFragment;

/**
 * Created by Mur0 on 5/2/2015.
 */
public class GalleryActivity extends AbsBaseActivity implements ObservableScrollViewCallbacks {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.gallery_acivity);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        seUpDefaultToolbar(toolbar);
        setUpDrawerToggle(drawerLayout);

        toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        setTint(getResources().getColor(R.color.colorPrimaryDark));


        final FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            GalleryDrawerFragment gdFragment = new GalleryDrawerFragment();
            GalleryFragment galleryFragment = new GalleryFragment();
            fm.beginTransaction().replace(R.id.drawer, gdFragment, gdFragment.getFragmentTag()).commit();
            fm.beginTransaction().replace(R.id.container, galleryFragment, galleryFragment.getFragmentTag()).commit();
        }

        startServiceUpdater();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                toggleDrawer(drawerLayout);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
//        final ActionBar ab = getSupportActionBar();
//        if (scrollState == ScrollState.UP) {
//            if (ab.isShowing()) {
//                ab.hide();
//            }
//        } else if (scrollState == ScrollState.DOWN) {
//            if (!ab.isShowing()) {
//                ab.show();
//            }
//        }
    }
}
