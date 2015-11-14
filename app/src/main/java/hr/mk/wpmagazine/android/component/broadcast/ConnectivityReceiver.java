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

package hr.mk.wpmagazine.android.component.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.greenrobot.event.EventBus;
import hr.mk.wpmagazine.model.BusEvents;

/**
 * Created by Triplus3 on 1/14/2015.
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new BusEvents(null, BusEvents.EvenType.CONNECTIVITY));
    }
}