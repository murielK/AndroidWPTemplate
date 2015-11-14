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

package hr.mk.wpmagazine.android.component.iterator;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import hr.mk.wpmagazine.Utils.ExecutorUtils;
import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.helper.RealmHelper;
import hr.mk.wpmagazine.model.WP.WPPost;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Mur0 on 5/30/2015.
 */
public class DataProviderImp implements DataProvider, RealmChangeListener {

    private static final String TAG = TagFactoryUtils.getTag(DataProviderImp.class);

    private final Realm realm;
    private final DataReceiver receiver;

    public DataProviderImp(@NonNull DataReceiver receiver) {
        this.receiver = receiver;
        realm = Realm.getInstance(receiver.provideActivity());
        realm.addChangeListener(this);
    }

    @Override
    public void queryAll() {
        ExecutorUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "First run query in background to cache");
                    long startTime = System.currentTimeMillis();
                    RealmHelper.findAll(receiver.provideActivity(), WPPost.class);
                    Log.d(TAG, "query in background done in: " + (System.currentTimeMillis() - startTime) + "ms");
                } catch (Exception e) {
                    Log.d(TAG, "", e);
                } finally {
                    final Activity activity = receiver.provideActivity();
                    if (activity != null)
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryAllMainPopulate();
                        }
                    });
                }

            }
        });
    }

    @Override
    public void queryArticles(final String field,final boolean filters) {

        ExecutorUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "First run query in background to cache");
                    long startTime = System.currentTimeMillis();
                    RealmHelper.queryEqualTo(receiver.provideActivity(), WPPost.class, field, filters);
                    Log.d(TAG, "query in background done in: " + (System.currentTimeMillis() - startTime) + "ms");
                } catch (Exception e) {
                    Log.d(TAG, "", e);
                } finally {
                    final Activity activity = receiver.provideActivity();
                    if (activity != null)
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryMainPopulate(field, filters);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void queryArticles(final String field,final String... filters) {

        ExecutorUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "First run query in background to cache");
                    long startTime = System.currentTimeMillis();
                    RealmHelper.queryEqualTo(receiver.provideActivity(), WPPost.class, field, filters);
                    Log.d(TAG, "query in background done in: " + (System.currentTimeMillis() - startTime) + "ms");
                } catch (Exception e) {
                    Log.d(TAG, "", e);
                } finally {
                    final Activity activity = receiver.provideActivity();
                    if (activity != null)
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryMainPopulate(field, filters);
                        }
                    });
                }

            }
        });
    }

    @Override
    public void queryArticlesContains(final String field,final String filters) {

        ExecutorUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "First run query in background to cache");
                    long startTime = System.currentTimeMillis();
                    RealmHelper.queryContains(receiver.provideActivity(), WPPost.class, field, filters);
                    Log.d(TAG, "query in background done in: " + (System.currentTimeMillis() - startTime) + "ms");
                } catch (Exception e) {
                    Log.d(TAG, "", e);
                } finally {
                    final Activity activity = receiver.provideActivity();
                    if (activity != null)
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryContainsMainPopulate(field, filters);
                        }
                    });
                }

            }
        });
    }


    private void queryContainsMainPopulate(final String field,final String... filters) {

        try {
            Log.d(TAG, "now same query running in mainThread");
            long startTime = System.currentTimeMillis();
            final RealmResults realmResults = RealmHelper.queryContains(realm, WPPost.class, field, filters);
            Log.d(TAG, "query in mainThread done in: " + (System.currentTimeMillis() - startTime) + "ms");
            receiver.onDataQueryDone(realmResults);
        } catch (Exception e) {
            receiver.onDataQueryFail();
            Log.d(TAG, "", e);
        }
    }


    private void queryAllMainPopulate() {

        try {
            Log.d(TAG, "now same query running in mainThread");
            long startTime = System.currentTimeMillis();
            final RealmResults realmResults = RealmHelper.findAll(realm, WPPost.class);
            Log.d(TAG, "query in mainThread done in: " + (System.currentTimeMillis() - startTime) + "ms");
            receiver.onDataQueryDone(realmResults);
        } catch (Exception e) {
            receiver.onDataQueryFail();
            Log.d(TAG, "", e);
        }

    }


    private void queryMainPopulate(final String field,final String... filters) {

        try {
            Log.d(TAG, "now same query running in mainThread");
            long startTime = System.currentTimeMillis();
            final RealmResults realmResults  = RealmHelper.queryEqualTo(realm, WPPost.class, field, filters);
            Log.d(TAG, "query in mainThread done in: " + (System.currentTimeMillis() - startTime) + "ms");
            receiver.onDataQueryDone(realmResults);
        } catch (Exception e) {
            receiver.onDataQueryFail();
            Log.d(TAG, "", e);
        }
    }


    private void queryMainPopulate(final String field,final boolean... filters) {

        try {
            Log.d(TAG, "now same query running in mainThread");
            long startTime = System.currentTimeMillis();
            final RealmResults realmResults  = RealmHelper.queryEqualTo(realm, WPPost.class, field, filters);
            Log.d(TAG, "query in mainThread done in: " + (System.currentTimeMillis() - startTime) + "ms");
            receiver.onDataQueryDone(realmResults);
        } catch (Exception e) {
            receiver.onDataQueryFail();
            Log.d(TAG, "", e);
        }
    }

    @Override
    public void onChange() {
        Log.d(TAG, "--- Realm data changed ---");
        receiver.onDataChanged();
    }

    public void recycle()// must be call when activity/fragment is killed to free resource and avoid memory leak
    {
        if (realm != null) {
            realm.removeChangeListener(this);
            realm.close();
        }

    }
}
