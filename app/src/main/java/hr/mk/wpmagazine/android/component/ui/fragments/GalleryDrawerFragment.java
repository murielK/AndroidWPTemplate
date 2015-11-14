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

import android.content.res.Resources;
import android.util.Log;

import de.greenrobot.event.EventBus;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.activities.FavoriteActivity;
import hr.mk.wpmagazine.android.component.activities.SettingActivity;
import hr.mk.wpmagazine.android.component.ui.adapters.SlidingMenuAdapter;
import hr.mk.wpmagazine.model.BusEvents;

/**
 * Created by Mur0 on 5/3/2015.
 */
public class GalleryDrawerFragment extends AbsDrawerFragment {

    @Override
    SlidingMenuAdapter provideMenuAdapter() {
        Log.d(getFragmentTag(), "Building Sliding menu");
        final Resources res = getActivity().getResources();
        final SlidingMenuAdapter.Builder builder = new SlidingMenuAdapter.Builder(getActivity());
        builder.addBanner(R.drawable.img_holder);

        final String[] catArrays = res.getStringArray(R.array.category_name);
        if (catArrays != null && catArrays.length > 0) {
            builder.addSeparator();
            builder.addHeader(res.getString(R.string.gallery));
            for (final String title : catArrays) {
                builder.addContent(title, 0);
            }
        }

        return builder.addSeparator()
                .addHeader(res.getString(R.string.visit_us))
                .addContent(res.getString(R.string.facebook), R.drawable.ic_menu_fb)
                .addContent(res.getString(R.string.twitter), R.drawable.ic_menu_twi)
                .addContent(res.getString(R.string.google_plus), R.drawable.ic_menu_gplus)
                .addSeparator()
                .addContent(res.getString(R.string.settings), R.drawable.ic_menu_setting)
                .addListner(this)
                .build();

    }

    @Override
    public void onMenuSelected(SlidingMenuAdapter.MenuObject menuObject) {

        final Resources res = getActivity().getResources();
        switch (menuObject.drawableResID) {
            case R.drawable.ic_menu_fb:
                openLinkIntent(res.getString(R.string.fb_link));
                break;
            case R.drawable.ic_menu_twi:
                openLinkIntent(res.getString(R.string.tw_link));
                break;
            case R.drawable.ic_menu_gplus:
                openLinkIntent(res.getString(R.string.g_link));
                break;
            case R.drawable.ic_menu_fav:
                startActivity(FavoriteActivity.class);
                break;
            case R.drawable.ic_menu_setting:
                startActivity(SettingActivity.class);
                break;
            default:
                try {
                    EventBus.getDefault().post(new BusEvents(menuObject.title, BusEvents.EvenType.GALLERY));
                } catch (Exception e) {
                    //Empty
                }
        }
    }
}
