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

package hr.mk.wpmagazine.android.component.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.concurrent.ConcurrentHashMap;

import hr.mk.wpmagazine.Utils.ConnectivityUtils;
import hr.mk.wpmagazine.Utils.PreferenceUtils;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.activities.AbsBaseActivity;
import hr.mk.wpmagazine.android.component.activities.FeedViewerActivity;
import hr.mk.wpmagazine.android.component.services.UpdaterService;
import hr.mk.wpmagazine.model.ObjectParcel;
import hr.mk.wpmagazine.model.WP.WPPost;

/**
 * Created by Mur0 on 3/29/2015.
 */
public class FeedsFragment extends AbsFeedFragment {

    public static final String TITLE_KEY = "TITLE_KEY";
    public static final String COLOR_DARK_KEY = "COLOR_DARK_KEY";
    public static final String COLOR_KEY = "COLOR_KEY";
    private static final ConcurrentHashMap<String, Integer> CACHE_IDS = new ConcurrentHashMap<>();
    private static final long DELAY = 1800000; /*1800000*/
    private static final String field = "terms.category.slug";

    protected String title;
    protected int color;
    protected int colorDark;
    private View padding;
    private boolean manualUpdate;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString(TITLE_KEY, "");
            color = savedInstanceState.getInt(COLOR_KEY);
            colorDark = savedInstanceState.getInt(COLOR_DARK_KEY);
        } else {
            title = getArguments().getString(TITLE_KEY, "");
            color = getArguments().getInt(COLOR_KEY);
            colorDark = getArguments().getInt(COLOR_DARK_KEY);
        }
        swipeRefreshLayout.setColorSchemeColors(color, colorDark);
        getArticles();
    }

    private void getArticles() {
        final String[] strings = getActivity().getResources().getStringArray(getArraysResID());
        queryArticles(field, strings);
    }

    private void saveLastUpdateMillis() {
        if (manualUpdate) {
            PreferenceUtils.putLong(getActivity(), PreferenceUtils.Preferences.LAST_UPDATE_MILLIS, System.currentTimeMillis());
            manualUpdate = false;
        }
    }

    @Override
    void onNoDataChange() {
        saveLastUpdateMillis();
    }

    @Override
    void onDataBaseUpdated() {
        saveLastUpdateMillis();
        super.onDataBaseUpdated();
    }

    @Override
    View providePadding() {
        if (padding == null)
        return LayoutInflater.from(getActivity()).inflate(R.layout.padding, null);
        return padding;
    }

    private boolean isOK() {
        final long lastUpdate = PreferenceUtils.getLong(getActivity(), PreferenceUtils.Preferences.LAST_UPDATE_MILLIS);
        final long current = System.currentTimeMillis();
        Log.d(getFragmentTag(), String.format("last update value: %d current: %d", lastUpdate, current));
        final long sinceLastUpdate = (current - lastUpdate); // 1000000;
        Log.d(getFragmentTag(), String.format("since last update value %d", sinceLastUpdate));
        if (sinceLastUpdate < DELAY) {
            toast(String.format(getActivity().getResources().getString(R.string.no_update), (DELAY - sinceLastUpdate) / 60000));
            return false;
        }
        if (!ConnectivityUtils.isOnline(getActivity())) {
            toast(getActivity().getResources().getString(R.string.no_internet_connection_retry));
            return false;
        }
        return true;
    }

    @Override
    boolean enableSwipeRefresh() {
        return true;
    }

    @Override
    void onSwipeRefresh() {
        if (!isOK()) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        AbsBaseActivity absBaseActivity = (AbsBaseActivity) getActivity();
        absBaseActivity.sendServiceCommand(UpdaterService.UPDATE_DATA_BASE);
        manualUpdate = true;
    }

    @Override
    boolean animateFirstOnly() {
        return true;
    }

    private int getArraysResID() {
        Log.d(getFragmentTag(), "looking id for: " + title);
        try {
            if (CACHE_IDS.containsKey(title)) {
                Log.d(getFragmentTag(), "cache id found for: " + title + ", with ID: " + CACHE_IDS.get(title));
                return CACHE_IDS.get(title);
            }
            final int resID = getResources().getIdentifier(title, "array", getActivity().getPackageName());
            CACHE_IDS.put(title, resID);
            return resID;
        } catch (Exception e) {
            Log.d(getFragmentTag(), "", e);
            return 0;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE_KEY, title);
        outState.putInt(COLOR_KEY, color);
        outState.putInt(COLOR_DARK_KEY, colorDark);
    }

    @Override
    void startActivity(int position, View v) {
        try {
            final WPPost wpPost = realmResults.get(position);
            Intent intent = new Intent(getActivity(), FeedViewerActivity.class);
            final ObjectParcel objectParcel = new ObjectParcel(wpPost.getID(), title, color, colorDark);
            intent.putExtra(FeedViewerActivity.OBJECT_KEY, objectParcel);
            startActivity(intent);
//            if (Build.VERSION.SDK_INT < 21)   // Disable for now
//                startActivity(intent);
//            else {
//                ImageView image = (ImageView) v.findViewById(R.id.image);
//                ViewCompat.setTransitionName(image,"image");
//                ActivityOptionsCompat options =
//                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
//                                image,   // The view which starts the transition
//                                "image"    // The transitionName of the view we’re transitioning to
//                        );
//                getActivity().startActivity(intent, options.toBundle());
//
//            }
        } catch (Exception e) {
            Log.d(getFragmentTag(), "", e);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    void onFavExecuted(int position, boolean ok) {
        final RecyclerView.Adapter adapter = getRecyclerViewAdapter();
        if (adapter == null)
            return;
        adapter.notifyItemChanged(position + 1);
        if (ok)
            toast(getResources().getString(R.string.fav_added));
        else
            toast(getResources().getString(R.string.fav_delete));
    }


}
