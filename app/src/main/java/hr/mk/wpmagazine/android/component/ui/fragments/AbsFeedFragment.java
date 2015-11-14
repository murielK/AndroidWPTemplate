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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import hr.mk.wpmagazine.Utils.ExecutorUtils;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.activities.FeedViewerActivity;
import hr.mk.wpmagazine.android.component.activities.FeedsActivity;
import hr.mk.wpmagazine.android.component.ui.adapters.FeedAdapter;
import hr.mk.wpmagazine.android.component.ui.adapters.WPAdapter;
import hr.mk.wpmagazine.helper.RealmHelper;
import hr.mk.wpmagazine.model.ObjectParcel;
import hr.mk.wpmagazine.model.WP.WPPost;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

/**
 * Created by Mur0 on 3/15/2015.
 */
abstract class AbsFeedFragment extends AbsWPPostsFragment {

    @Override
    WPAdapter provideAdapter() {
        return new FeedAdapter(getActivity(), providePadding(), realmResults);
    }

    abstract View providePadding();

    abstract void onFavExecuted(final int position, final boolean ok);

    @Override
    protected void onPostViewCreate() {
        super.onPostViewCreate();
        if (providePadding() != null) {
            swipeRefreshLayout.setProgressViewOffset(false, getActivity().getResources().getDimensionPixelSize(R.dimen.tab_height), getActivity().getResources().getDimensionPixelSize(R.dimen.tab_height) * 2);
        }
    }

    @Override
    void initViews() {
        final Activity parentActivity = getActivity();
        final int spanCount = parentActivity.getResources().getInteger(R.integer.span_count);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(parentActivity, spanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (position) {
                    case 0:
                        return providePadding() == null ? 1 : spanCount;
                    default:
                        return parentActivity.getResources().getInteger(R.integer.span_size);
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(false);
        ScaleInAnimator scaleInAnimator = new ScaleInAnimator();
        scaleInAnimator.setAddDuration(350);
        scaleInAnimator.setRemoveDuration(350);
        scaleInAnimator.setMoveDuration(350);
        scaleInAnimator.setChangeDuration(350);
        recyclerView.setItemAnimator(scaleInAnimator);
        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            final Bundle arg = getArguments();
            if (arg != null && arg.containsKey(FeedsActivity.ARG_INITIAL_POSITION)) {
                final int initPosition = arg.getInt(FeedsActivity.ARG_INITIAL_POSITION, 0);
                ScrollUtils.addOnGlobalLayoutListener(recyclerView, new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.scrollVerticallyToPosition(initPosition);
                    }
                });
            }
            recyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
    }

    void startActivity(int position, View v) {
        try {
            final WPPost wpPost = realmResults.get(position);
            Intent intent = new Intent(getActivity(), FeedViewerActivity.class);
            String category = "";
            try {
                category = wpPost.getTerms().getCategory().get(0).getName();
            } catch (Exception e) {
                //Empty
            }
            final ObjectParcel objectParcel = new ObjectParcel(wpPost.getID(), category, getResources().getColor(R.color.primaryColor), getResources().getColor(R.color.colorPrimaryDark));
            intent.putExtra(FeedViewerActivity.OBJECT_KEY, objectParcel);
            startActivity(intent);
        } catch (Exception e) {
            Log.d(getFragmentTag(), "", e);
        }

    }

    void executeFav(final int position) {
        final int ID = realmResults.get(position).getID();
        final boolean fav = realmResults.get(position).isFavorite();
        ExecutorUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final boolean ok = RealmHelper.writeFavorite(getActivity(), ID, !fav);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onFavExecuted(position, ok);
                    }
                });
            }
        });
    }

    void executeShare(final int position) {
        try {
            final String link = realmResults.get(position).getLink();
            final String title = realmResults.get(position).getTitle();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "LWN Magazine: " + Html.fromHtml(title) + " " + link);
            startActivity(Intent.createChooser(intent, getActivity().getResources().getString(R.string.share_with)));
        } catch (Exception e) {
            Log.d(getFragmentTag(), getActivity().getResources().getString(R.string.share_fail));
            toast(getActivity().getResources().getString(R.string.share_fail));
        }
    }


    @Override
    public void onClick(View v, int position) {
        final int id = v.getId();
        switch (id) {
            case R.id.layoutFavorite:
                executeFav(position);
                break;
            case R.id.layoutShare:
                executeShare(position);
                break;
            default:
                startActivity(position, v);
                break;
        }
    }

}
