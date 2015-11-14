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

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.InjectView;
import hr.mk.wpmagazine.android.component.R;

/**
 * Created by Mur0 on 3/31/2015.
 */
public class SearchFragment extends AbsFeedFragment {

    @InjectView(R.id.imageView)
    ImageView imageView;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.textView)
    TextView textView;

    @Override
    protected int getResourceView() {
        return R.layout.search_fragment;
    }

    public void search(String query) {
        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        updateRealmResultToAdapter(null);
        queryArticlesContains("title", query);
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
    void onFavExecuted(final int position, final boolean ok) {
        final RecyclerView.Adapter adapter = getRecyclerViewAdapter();
        if (adapter == null)
            return;
        adapter.notifyItemChanged(position);
        if (ok)
            toast(getResources().getString(R.string.fav_added));
        else
            toast(getResources().getString(R.string.fav_delete));

        final ActionBarActivity actionBarActivity = (ActionBarActivity) getActivity();
        final ActionBar ab = actionBarActivity.getSupportActionBar();
        if (!ab.isShowing())
            ab.show();
    }

    @Override
    void onQueryFail() {
        onDataPopulated();
    }

    @Override
    protected void onDataPopulated() {
        progressBar.setVisibility(View.GONE);
        if (realmResults.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    boolean animateFirstOnly() {
        return false;
    }

}
