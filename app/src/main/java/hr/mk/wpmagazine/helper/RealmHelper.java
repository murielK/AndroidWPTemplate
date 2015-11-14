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

package hr.mk.wpmagazine.helper;

import android.content.Context;
import android.util.Log;

import java.util.List;

import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.model.WP.WPPost;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Mur0 on 3/15/2015.
 */
public final class RealmHelper {

    private final static String TAG = TagFactoryUtils.getTag(RealmHelper.class);

    private RealmHelper() {
    }

    public static <E extends RealmObject> RealmResults<E> queryEqualTo(final Context context, final Class<E> eClass, final String field, final String... filters) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(context);
            return queryEqualTo(realm, eClass, field, filters);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        } finally {
            if (realm != null)
                realm.close();
        }

    }

    public static <E extends RealmObject> RealmResults<E> queryEqualTo(final Context context, final Class<E> eClass, final String field, final boolean... filters) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(context);
            return queryEqualTo(realm, eClass, field, filters);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        } finally {
            if (realm != null)
                realm.close();
        }

    }

    public static <E extends RealmObject> RealmResults<E> queryEqualTo(final Context context, final Class<E> eClass, final String field, final int... filters) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(context);
            return queryEqualTo(realm, eClass, field, filters);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        } finally {
            if (realm != null)
                realm.close();
        }

    }

    public static <E extends RealmObject> RealmResults<E> queryEqualTo(final Realm realm, final Class<E> eClass, final String field, final boolean... filters) {

        try {
            final RealmQuery realmQuery = realm.where(eClass);
            int i = 0;
            for (final boolean filter : filters) {
                if (i != 0)
                    realmQuery.or();
                realmQuery.equalTo(field, filter);
                i++;
            }
            return getRealmResults(realmQuery);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        }

    }

    public static <E extends RealmObject> RealmResults<E> queryEqualTo(final Realm realm, final Class<E> eClass, final String field, final String... filters) {

        try {
            final RealmQuery realmQuery = realm.where(eClass);
            int i = 0;
            for (final String filter : filters) {
                if (i != 0)
                    realmQuery.or();
                realmQuery.equalTo(field, filter);
                i++;
            }
            return getRealmResults(realmQuery);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        }

    }

    public static <E extends RealmObject> RealmResults<E> queryEqualTo(final Realm realm, final Class<E> eClass, final String field, final int... filters) {

        try {
            final RealmQuery realmQuery = realm.where(eClass);
            int i = 0;
            for (final int filter : filters) {
                if (i != 0)
                    realmQuery.or();
                realmQuery.equalTo(field, filter);
                i++;
            }
            return getRealmResults(realmQuery);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        }
    }

    public static <E extends RealmObject> RealmResults<E> queryContains(final Context context, final Class<E> eClass, final String field, final String... filters) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(context);
            return queryContains(realm, eClass, field, filters);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        } finally {
            if (realm != null)
                realm.close();
        }

    }

    public static <E extends RealmObject> RealmResults<E> queryContains(final Realm realm, final Class<E> eClass, final String field, final String... filters) {

        try {
            final RealmQuery realmQuery = realm.where(eClass);
            int i = 0;
            for (final String filter : filters) {
                if (i != 0)
                    realmQuery.or();
                realmQuery.contains(field, filter, false);
                i++;
            }
            return getRealmResults(realmQuery);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        }

    }

    public static RealmResults getRealmResults(final RealmQuery realmQuery) {
        try {
            return realmQuery.findAll();
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        }
    }

    public static <E extends RealmObject> RealmResults findAll(final Realm realm, final Class<E> eClass) {
        try {
            return realm.where(eClass).findAll();
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        }
    }

    public static <E extends RealmObject> RealmResults findAll(final Context context, final Class<E> eClass) {
        Realm realm = null;

        try {
            realm = Realm.getInstance(context);
            return findAll(realm, eClass);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return null;
        } finally {
            if (realm != null)
                realm.close();
        }
    }


    public static <E extends RealmObject> boolean realmWrite(final Context context, final List<E> objects) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(context);
            return realmWrite(realm, objects);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return false;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public static <E extends RealmObject> boolean realmWrite(final Realm realm, final List<E> objects) {
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(objects);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return false;
        }

    }

    public static boolean realmWrite(final Realm realm, final RealmObject object) {
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(object);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return false;
        }

    }

    public static boolean writeFavorite(final Context context, final int ID, final boolean fav) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(context);
            RealmResults<WPPost> realmResults = realm.where(WPPost.class).equalTo("ID", ID).findAll();
            realm.beginTransaction();
            realmResults.get(0).setFavorite(fav);
            realm.commitTransaction();
            return fav;
        } catch (Exception e) {
            //
            return false;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public static boolean realmWrite(final Context context, final RealmObject object) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(context);
            return realmWrite(realm, object);
        } catch (Exception e) {
            Log.d(TAG, "", e);
            return false;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

}
