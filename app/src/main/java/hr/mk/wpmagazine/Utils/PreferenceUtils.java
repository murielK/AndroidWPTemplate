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

package hr.mk.wpmagazine.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Mur0 on 3/18/2015.
 */
public class PreferenceUtils {

    private PreferenceUtils() {

    }

    public static void putString(final Context context, final Preferences preferences, final String value) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(preferences.name(), value).commit();
    }

    public static void putBoolean(final Context context, final Preferences preferences, final boolean value) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(preferences.name(), value).commit();
    }

    public static void putLong(final Context context, final Preferences preferences, final long value) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(preferences.name(), value).commit();
    }

    public static void putInt(final Context context, final Preferences preferences, final int value) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(preferences.name(), value).commit();
    }

    public static String getString(final Context context, final Preferences preferences) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(preferences.name(), "");
    }

    public static boolean getBoolean(final Context context, final Preferences preferences) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(preferences.name(), false);
    }

    public static long getLong(final Context context, final Preferences preferences) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(preferences.name(), 0);
    }

    public static int getInt(final Context context, final Preferences preferences) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(preferences.name(), 0);
    }


    public enum Preferences {

        DATA_BASE_LOADED, LAST_UPDATE, LAST_UPDATE_MILLIS, PROPERTY_REG_ID, NOTIFICATION_ON, NOTIFICATION_SOUND, NOTIFICATION_VIBRATION, PROPERTY_APP_VERSION, DOWNLOAD_IMAGES
    }

}
