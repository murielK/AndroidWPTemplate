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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.model.BusEvents;

/**
 * Created by Mur0 on 3/15/2015.
 */
public abstract class AbsBaseFragment extends Fragment {

    protected String tag;
    protected Handler handler;
    protected EventBus eventBus;
    private ProgressDialog progressDialog;

    private boolean isDestroyed;

    public String getFragmentTag() {
        if (tag == null)
            tag = TagFactoryUtils.getTag(this);
        return tag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        eventBus = EventBus.getDefault();
    }

    protected abstract int getResourceView();

    protected abstract void onPostViewCreate();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getResourceView() == 0) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        final View v = inflater.inflate(getResourceView(), null);
        ButterKnife.inject(this, v);
        onPostViewCreate();
        return v;

    }

    protected void showProgressDialog(String message, boolean cancelable) {
        if (isDestroyed)
            return;
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(cancelable);
        } else {
            progressDialog.setMessage(message);
        }

        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    protected void dismissProgressDialog() {
        if ((progressDialog != null) & !isDestroyed) {
            progressDialog.dismiss();
        }
    }

    protected void toast(final String message) {
        try {
            if (!isDetached() || !isDestroyed) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d(getFragmentTag(), "", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        eventBus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        handler = null;
    }

    public void onEventMainThread(BusEvents event) {


    }

}
