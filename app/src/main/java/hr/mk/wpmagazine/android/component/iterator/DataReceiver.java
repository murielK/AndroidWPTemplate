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

import io.realm.RealmResults;

/**
 * Created by Mur0 on 5/30/2015.
 */
public interface DataReceiver {

    void onDataQueryDone(RealmResults realmResults);

    void onDataQueryFail();

    void onDataChanged();

    Activity provideActivity();

}
