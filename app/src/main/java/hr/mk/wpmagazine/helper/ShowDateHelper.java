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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hr.mk.wpmagazine.android.component.R;

/**
 * Created by Mur0 on 3/28/2015.
 */
public class ShowDateHelper {

    private static final int MIN_IN_MILLIS = 1000 * 60;
    private static final int HOUR_IN_MILLIS = MIN_IN_MILLIS * 60;

    private final Calendar calIn;
    private final Calendar now;
    private final Context context;
    private final DateFormat formatDate;
    private final DateFormat formatTime;

    public ShowDateHelper(Context context) {
        this.calIn = Calendar.getInstance();
        this.now = Calendar.getInstance();
        this.context = context;
        formatDate = new SimpleDateFormat("EEEE dd MMM yyyy HH:mm", context.getResources().getConfiguration().locale);
        formatTime = new SimpleDateFormat("HH:mm", context.getResources().getConfiguration().locale);
    }

    public String getFormattedDate(final Date date) {
        if (date == null)
            return "";

        calIn.setTimeInMillis(date.getTime());
        now.setTimeInMillis(System.currentTimeMillis());

        if (now.get(Calendar.YEAR) != calIn.get(Calendar.YEAR))
            return formatDate.format(date);

        final int range = now.get(Calendar.DAY_OF_YEAR) - calIn.get(Calendar.DAY_OF_YEAR);
        switch (range) {
            case 0:
                long elapseMillis = now.getTimeInMillis() - calIn.getTimeInMillis();
                elapseMillis = elapseMillis < 0 ? 0 : elapseMillis;
                if (elapseMillis < MIN_IN_MILLIS) {

                    elapseMillis /= 1000;
                    return elapseMillis > 1 ? context.getResources().getString(R.string.date_sec_holder_pl).replace("{sec}", String.valueOf(elapseMillis)) : context.getResources().getString(R.string.date_sec_holder).replace("{sec}", String.valueOf(elapseMillis));

                } else if (elapseMillis < HOUR_IN_MILLIS) {

                    elapseMillis /= MIN_IN_MILLIS;
                    return elapseMillis > 1 ? context.getResources().getString(R.string.date_min_holder_pl).replace("{min}", String.valueOf(elapseMillis)) : context.getResources().getString(R.string.date_min_holder).replace("{min}", String.valueOf(elapseMillis));

                } else {

                    elapseMillis /= HOUR_IN_MILLIS;
                    return elapseMillis > 1 ? context.getResources().getString(R.string.date_hours_holder_pl).replace("{hour}", String.valueOf(elapseMillis)) : context.getResources().getString(R.string.date_hours_holder).replace("{hour}", String.valueOf(elapseMillis));

                }
            case 1:
                return context.getResources().getString(R.string.yesterday) + " " + context.getResources().getString(R.string.at) + " " + formatTime.format(date);
            case 2:
            case 3:
                final String temp = context.getResources().getString(R.string.date_day_holder_pl).replace("{day}", String.valueOf(range));
                return temp + " " + context.getResources().getString(R.string.at) + " " + formatTime.format(date);
            default:
                return formatDate.format(date);
        }
    }
}
