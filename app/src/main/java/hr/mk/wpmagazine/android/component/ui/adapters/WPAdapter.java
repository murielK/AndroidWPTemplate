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

package hr.mk.wpmagazine.android.component.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import hr.mk.wpmagazine.model.WP.WPPost;
import io.realm.RealmResults;

/**
 * Created by Mur0 on 5/2/2015.
 */
public abstract class WPAdapter extends RecyclerView.Adapter {

    public abstract void setListener(WPAdapterOnClickListener listener);

    public abstract void flush();

    public abstract void removeListener();

    public abstract void updateRealmResult(RealmResults<WPPost> realmResults);

    public interface WPAdapterOnClickListener {

        void onClick(View v, int position);

    }
}
