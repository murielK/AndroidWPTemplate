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
import android.graphics.Typeface;
import android.support.v4.util.ArrayMap;
import android.util.Log;

/**
 * Created by Mur0 on 4/1/2015.
 */
public class TypeFaceUtils {

    private static ArrayMap<String, Typeface> map = new ArrayMap<>();
    private static String TAG = TagFactoryUtils.getTag(TypeFaceUtils.class);

    private TypeFaceUtils() {
    }

    public static Typeface getTypeFace(Context context, String path) {
        if (!map.containsKey(path)) {
            synchronized (TypeFaceUtils.class) {
                if (!map.containsKey(path)) {
                    try {
                        final Typeface typeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(), path);
                        map.put(path, typeface);
                    } catch (Exception e) {
                        Log.d(TAG, "", e);
                    }
                }
            }
        }
        return map.get(path);
    }

    public static void clear() {
        map.clear();
    }
}
