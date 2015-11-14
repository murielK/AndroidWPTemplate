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

package hr.mk.wpmagazine.android.component.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.InjectView;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.ui.adapters.SlidingMenuAdapter;

/**
 * Created by Mur0 on 3/21/2015.
 */
public abstract class AbsDrawerFragment extends AbsBaseFragment implements SlidingMenuAdapter.SlidingMenuListener {

    @InjectView(R.id.scrollDrawer)
    RecyclerView recyclerView;

    abstract SlidingMenuAdapter provideMenuAdapter();

    @Override
    protected int getResourceView() {
        return R.layout.fragment_drawer;
    }

    @Override
    protected void onPostViewCreate() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(provideMenuAdapter());
    }

    void startActivity(Class cls) {
        final Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }

    void openLinkIntent(String link) {
        final String url = link;
        final Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(Intent.createChooser(i, getActivity().getResources().getString(R.string.open_with)));
    }

}
