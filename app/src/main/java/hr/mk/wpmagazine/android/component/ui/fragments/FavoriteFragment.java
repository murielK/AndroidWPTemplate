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

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.InjectView;
import hr.mk.wpmagazine.Utils.TypeFaceUtils;
import hr.mk.wpmagazine.android.component.R;

/**
 * Created by Mur0 on 3/29/2015.
 */
public class FavoriteFragment extends AbsFeedFragment {

    @InjectView(R.id.textView)
    TextView textView;

    private boolean wasPaused;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Typeface typeface = TypeFaceUtils.getTypeFace(getActivity(), "OpenSans-Regular.ttf");
        if (typeface != null)
            textView.setTypeface(typeface);
        queryArticles("favorite", true);
    }

    @Override
    View providePadding() {
        return null;
    }

    @Override
    boolean enableSwipeRefresh() {
        return false;
    }


    @Override
    protected int getResourceView() {
        return R.layout.favorite_layout;
    }

    @Override
    void onQueryFail() {
        onDataPopulated();
    }

    @Override
    void onDataPopulated() {
        if (realmResults == null || realmResults.isEmpty())
            textView.setVisibility(View.VISIBLE);
        else
            textView.setVisibility(View.GONE);
    }

    @Override
    boolean animateFirstOnly() {
        return true;
    }

    @Override
    void onFavExecuted(final int position, final boolean ok) {
        final RecyclerView.Adapter adapter = getRecyclerViewAdapter();
        if (adapter == null)
            return;
        if (ok) {
            toast(getResources().getString(R.string.fav_added));
            adapter.notifyItemChanged(position);
        } else
            adapter.notifyItemRemoved(position);
        final AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        final ActionBar ab = appCompatActivity.getSupportActionBar();
        if (ab != null && !ab.isShowing())
            ab.show();
        onDataPopulated();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (wasPaused)
            onDataPopulated();
    }

    @Override
    public void onPause() {
        super.onPause();
        wasPaused = true;
    }

}
