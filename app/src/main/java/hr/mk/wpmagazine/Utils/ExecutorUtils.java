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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mur0 on 3/22/2015.
 */
public class ExecutorUtils {

    private static final int THREAD_POOL = 5;
    private static ExecutorService executor;

    private ExecutorUtils() {
    }

    public static ExecutorService getExecutor() {
        if (executor == null) {
            synchronized (ExecutorUtils.class) {
                if (executor == null)
                    executor = Executors.newFixedThreadPool(THREAD_POOL);
            }
        }
        return executor;
    }
}
