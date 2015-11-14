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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.ui.adapters.GalleryAdapter;
import hr.mk.wpmagazine.android.component.ui.adapters.WPAdapter;
import hr.mk.wpmagazine.model.BusEvents;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

/**
 * Created by Mur0 on 5/3/2015.
 */
public class GalleryFragment extends AbsWPPostsFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryAll();
    }

    @Override
    void initViews() {
        final Activity parentActivity = getActivity();
        final int spanCount = parentActivity.getResources().getInteger(R.integer.span_count);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                return parentActivity.getResources().getInteger(R.integer.span_size);

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
            recyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
    }

    @Override
    boolean enableSwipeRefresh() {
        return false;
    }

    @Override
    WPAdapter provideAdapter() {
        return new GalleryAdapter(getActivity(), realmResults);
    }

    @Override
    boolean animateFirstOnly() {
        return true;
    }

    @Override
    public void onClick(View v, int position) {

        //TODO launch activity image viewer here!!!! :D :D :D :D :D
    }

    private void update(String filer) {
        queryArticles("title", filer);
    }

    @Override
    public void onEventMainThread(BusEvents event) {
        if (event.evenType == BusEvents.EvenType.GALLERY) {
            try {
                update((String) event.object);
            } catch (Exception e) {
                Log.d(getFragmentTag(), "", e);
            }
        } else
            super.onEventMainThread(event);

    }

}
