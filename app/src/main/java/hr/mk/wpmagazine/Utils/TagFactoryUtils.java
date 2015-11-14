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

/**
 * Created by Mur0 on 3/15/2015.
 */
public final class TagFactoryUtils {

    private static final String TAG_PREFIX = "WPT_";

    private TagFactoryUtils() {
    }

    public static String getTag(final Object obj) {
        final Class cls = obj.getClass();
        return getTag(cls.getSimpleName());

    }

    public static String getTag(final Class cls) {
        return getTag(cls.getSimpleName());

    }

    public static String getTag(final String suffix) {
        return TAG_PREFIX + suffix;
    }
}
