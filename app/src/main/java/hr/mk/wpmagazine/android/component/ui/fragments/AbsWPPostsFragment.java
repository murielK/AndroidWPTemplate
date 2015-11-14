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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

import butterknife.InjectView;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.iterator.DataProvider;
import hr.mk.wpmagazine.android.component.iterator.DataProviderImp;
import hr.mk.wpmagazine.android.component.iterator.DataReceiver;
import hr.mk.wpmagazine.android.component.ui.adapters.WPAdapter;
import hr.mk.wpmagazine.model.BusEvents;
import hr.mk.wpmagazine.model.WP.WPPost;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Created by Mur0 on 5/2/2015.
 */
public abstract class AbsWPPostsFragment extends AbsBaseFragment implements WPAdapter.WPAdapterOnClickListener, DataProvider, DataReceiver {

    protected RealmResults<WPPost> realmResults;
    protected boolean isDestroy;
    protected boolean callNotify;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.scroll)
    ObservableRecyclerView recyclerView;
    private WPAdapter wpAdapter;
    private DataProvider dataProvider;

    abstract void initViews();

    abstract boolean animateFirstOnly();

    abstract boolean enableSwipeRefresh();

    abstract WPAdapter provideAdapter();

    void onQueryFail() {//to be override in case

    }

    void onDataPopulated() {//to be override in case

    }

    void onNoDataChange() {//to be override in case when a refresh is requested and executed properly but with no data changed

    }

    void onDataBaseUpdateFail() {

    }

    void onSwipeRefresh() {//to be override in case

    }

    @Override
    protected int getResourceView() {
        return R.layout.fragment_recent;
    }

    @Override
    protected void onPostViewCreate() {
        if (!enableSwipeRefresh())
            swipeRefreshLayout.setEnabled(false);

        swipeRefreshLayout.setDistanceToTriggerSync((int) getActivity().getResources().getDimension(R.dimen.dis_to_sync));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onSwipeRefresh();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataProvider = new DataProviderImp(this);
        initViews();
    }

    @Override
    public void queryAll() {
        dataProvider.queryAll();
    }

    @Override
    public void queryArticles(final String field, final boolean filters) {
        dataProvider.queryArticles(field, filters);
    }


    @Override
    public void queryArticles(final String field, final String... filters) {
        dataProvider.queryArticles(field, filters);
    }

    @Override
    public void queryArticlesContains(final String field, final String filters) {
        dataProvider.queryArticlesContains(field, filters);
    }

    void onDataBaseUpdated() {
        RecyclerView.Adapter adapter = getRecyclerViewAdapter();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void setAdapter() {

        if (wpAdapter == null) {

            wpAdapter = provideAdapter();

            if (wpAdapter == null)
                throw new NullPointerException("Please provide a non null adapter");

            wpAdapter.setListener(this);
            ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(wpAdapter);
            scaleInAnimationAdapter.setDuration(350);
            scaleInAnimationAdapter.setFirstOnly(animateFirstOnly());
            recyclerView.setAdapter(scaleInAnimationAdapter);

        } else {

            updateRealmResultToAdapter(realmResults); // always refresh the main adapter when updating wpAdapter; too bad that the scaleAdapter lib cant return me the set adapter
        }

        callNotify = false;
    }

    void updateRealmResultToAdapter(RealmResults<WPPost> realmResults) {
        if (wpAdapter == null || recyclerView == null)
            return;

        wpAdapter.updateRealmResult(realmResults);
        recyclerView.getAdapter().notifyDataSetChanged();

    }

    RecyclerView.Adapter getRecyclerViewAdapter() {// need to added this for this stupid animation lib until i implement my own
        if (recyclerView != null)
            return recyclerView.getAdapter();
        return null;
    }

    void sortRealResultByDate() {
        sortRealmResult("date", false);
    }

    void sortRealmResult(final String field, boolean sortAscending) {
        if (realmResults != null) // should never be as Realm return an empty list<E extends RealObject> in case it finds nothing!!! Just this IDEA to stop bitching
            realmResults.sort(field, sortAscending);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wpAdapter != null && callNotify) {
            // wpAdapter.notifyDataSetChanged();
            recyclerView.getAdapter().notifyDataSetChanged();
            Log.d(getFragmentTag(), "notifyDataSetChanged called at onResume()");
        }
    }

    @Override
    public void onDestroy() {
        isDestroy = true;
        //Memory leak is a bitch
        if (wpAdapter != null) {
            wpAdapter.flush();//jake wharton said unused ram is wasted ram! Do i really need to that?
            wpAdapter.removeListener();
        }

        if (recyclerView != null) {
            recyclerView.setAdapter(null);
            recyclerView.setScrollViewCallbacks(null);
        }

        ((DataProviderImp) dataProvider).recycle();

        recyclerView = null;
        wpAdapter = null;
        realmResults = null;

        super.onDestroy();
    }

    @Override
    public void onPause() {
        callNotify = true;
        super.onPause();
    }

    @Override
    public void onEventMainThread(BusEvents event) {
        if (event.evenType == BusEvents.EvenType.DATA_BASE_UPDATED || event.evenType == BusEvents.EvenType.DATA_BASE_UPDATED_FAIL || event.evenType == BusEvents.EvenType.DATA_BASE_IS_UP_TO_DATE) {
            swipeRefreshLayout.setRefreshing(false);
            if (event.evenType == BusEvents.EvenType.DATA_BASE_UPDATED)
                onDataBaseUpdated();
            else if (event.evenType == BusEvents.EvenType.DATA_BASE_IS_UP_TO_DATE)
                onNoDataChange();
            else
                onDataBaseUpdateFail();
        }

    }

    @Override
    public void onDataQueryDone(RealmResults realmResults) {
        this.realmResults = realmResults;
        sortRealResultByDate();
        setAdapter();
        onDataPopulated();
    }

    @Override
    public void onDataQueryFail() {
        onQueryFail();
    }

    @Override
    public void onDataChanged() {
        // Empty
    }

    @Override
    public Activity provideActivity() {
        return getActivity();
    }
}
