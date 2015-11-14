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

package hr.mk.wpmagazine.android.component.ui.widget;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Mur0 on 3/22/2015.
 * Custom drawerToggle that can re-dispatch drawer event
 */
public class ObsABDrawerToggle extends ActionBarDrawerToggle {

    private ObservableDrawerListener listener;

    public ObsABDrawerToggle(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    public ObsABDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    public void setObservableDrawerListener(ObservableDrawerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        if (listener != null)
            listener.onDrawerOpened(drawerView);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
        if (listener != null)
            listener.onDrawerSlide(drawerView, slideOffset);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        if (listener != null)
            listener.onDrawerClosed(drawerView);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        super.onDrawerStateChanged(newState);
        if (listener != null)
            listener.onDrawerStateChanged(newState);
    }


    public interface ObservableDrawerListener {
        /**
         * Called when a drawer's position changes.
         *
         * @param drawerView  The child view that was moved
         * @param slideOffset The new offset of this drawer within its range, from 0-1
         */
        void onDrawerSlide(View drawerView, float slideOffset);

        /**
         * Called when a drawer has settled in a completely open state.
         * The drawer is interactive at this point.
         *
         * @param drawerView Drawer view that is now open
         */
        void onDrawerOpened(View drawerView);

        /**
         * Called when a drawer has settled in a completely closed state.
         *
         * @param drawerView Drawer view that is now closed
         */
        void onDrawerClosed(View drawerView);

        /**
         * Called when the drawer motion state changes. The new state will
         *
         * @param newState The new drawer motion state
         */
        void onDrawerStateChanged(int newState);
    }
}
