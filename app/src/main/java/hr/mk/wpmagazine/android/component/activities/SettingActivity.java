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

package hr.mk.wpmagazine.android.component.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import hr.mk.wpmagazine.Utils.PreferenceUtils;
import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.Utils.TypeFaceUtils;
import hr.mk.wpmagazine.android.component.R;

/**
 * Created by Mur0 on 4/5/2015.
 */
public class SettingActivity extends AbsBaseActivity {

    private static final String TAG = TagFactoryUtils.getTag(SettingActivity.class);


    @InjectView(R.id.textViewAPPVersion)
    TextView version;
    @InjectView(R.id.textViewAPPVValue)
    TextView versionName;
    @InjectView(R.id.textViewDev)
    TextView dev;
    @InjectView(R.id.textViewDevValue)
    TextView devName;
    @InjectView(R.id.textViewDesigner)
    TextView designer;
    @InjectView(R.id.textView5DesValue)
    TextView designerName;


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.textViewPushEnable)
    TextView textViewPushEnable;
    @InjectView(R.id.checkBoxPush)
    CheckBox checkBoxPush;

    @InjectView(R.id.textViewSound)
    TextView textViewSound;
    @InjectView(R.id.checkBoxSound)
    CheckBox checkBoxSound;

    @InjectView(R.id.textViewVib)
    TextView textViewVib;
    @InjectView(R.id.checkBoxVib)
    CheckBox checkBoxVib;

    @InjectView(R.id.checkBoxDownloadImage)
    CheckBox checkBoxDowloadImage;

    @InjectView(R.id.textViewNotification)
    TextView textViewNotification;

    @InjectView(R.id.textViewAbout)
    TextView textViewAbout;


    private void setViews() {
        if (PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_ON)) {
            textViewSound.setEnabled(true);
            textViewVib.setEnabled(true);
            checkBoxSound.setEnabled(true);
            checkBoxVib.setEnabled(true);
        } else {
            textViewSound.setEnabled(false);
            checkBoxSound.setEnabled(false);
            textViewVib.setEnabled(false);
            checkBoxVib.setEnabled(false);
        }
        checkBoxPush.setChecked(PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_ON));
        checkBoxSound.setChecked(PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_SOUND));
        checkBoxVib.setChecked(PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_VIBRATION));
        checkBoxDowloadImage.setChecked(PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.DOWNLOAD_IMAGES));
    }

    @OnCheckedChanged(R.id.checkBoxPush)
    public void onCheckPushChange(CompoundButton buttonView, boolean isChecked) {
        PreferenceUtils.putBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_ON, isChecked);
        setViews();
    }

    @OnCheckedChanged(R.id.checkBoxSound)
    public void onCheckSoundChange(CompoundButton buttonView, boolean isChecked) {
        PreferenceUtils.putBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_SOUND, isChecked);
    }

    @OnCheckedChanged(R.id.checkBoxVib)
    public void onCheckVibChange(CompoundButton buttonView, boolean isChecked) {
        PreferenceUtils.putBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_VIBRATION, isChecked);
    }

    @OnCheckedChanged(R.id.checkBoxDownloadImage)
    public void onCheckDownloadImage(CompoundButton buttonView, boolean isChecked) {
        PreferenceUtils.putBoolean(this, PreferenceUtils.Preferences.DOWNLOAD_IMAGES, isChecked);
    }

//    @OnClick(R.id.textViewAbout)
//    public void onAboutClick(View v) {
//        showAbout();
//    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.setting_activity);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        init();
        setViews();
    }

    protected void init() { // will move this to absActivity
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        setTint(getResources().getColor(R.color.colorPrimaryDark));
        toolbar.setTitle("");
        try {
            final Typeface typeface = TypeFaceUtils.getTypeFace(this, "OpenSans-Regular.ttf");
            final Typeface typefaceSemiBold = TypeFaceUtils.getTypeFace(this, "OpenSans-Semibold.ttf");
            textViewAbout.setTypeface(typeface);
            textViewPushEnable.setTypeface(typeface);
            textViewSound.setTypeface(typeface);
            textViewVib.setTypeface(typeface);
            textViewNotification.setTypeface(typeface);
            version.setTypeface(typefaceSemiBold);
            dev.setTypeface(typefaceSemiBold);
            designer.setTypeface(typefaceSemiBold);
            versionName.setTypeface(typeface);
            devName.setTypeface(typeface);
            designerName.setTypeface(typeface);
            versionName.setText(getAppVersion());
        } catch (Exception e) {
            //
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getAppVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "", e);
        }

        return packageInfo == null ? null : packageInfo.versionName;
    }

    private void showAbout() {

        final String appVersion = getAppVersion();
        if (appVersion == null)
            return;

        final View v = LayoutInflater.from(this).inflate(R.layout.layout_about, null);
        final TextView version = (TextView) v.findViewById(R.id.textViewAPPVersion);
        final TextView textViewAbout = (TextView) v.findViewById(R.id.textViewAbouyTitle);
        final TextView versionName = (TextView) v.findViewById(R.id.textViewAPPVValue);
        final TextView dev = (TextView) v.findViewById(R.id.textViewDev);
        final TextView devName = (TextView) v.findViewById(R.id.textViewDevValue);
        final TextView designer = (TextView) v.findViewById(R.id.textViewDesigner);
        final TextView designerName = (TextView) v.findViewById(R.id.textView5DesValue);

        try {
            Typeface typeface = TypeFaceUtils.getTypeFace(this, "OpenSans-Regular.ttf");
            Typeface typefaceSemiBold = TypeFaceUtils.getTypeFace(this, "OpenSans-Semibold.ttf");
            textViewAbout.setTypeface(typefaceSemiBold);
            version.setTypeface(typefaceSemiBold);
            dev.setTypeface(typefaceSemiBold);
            designer.setTypeface(typefaceSemiBold);
            versionName.setTypeface(typeface);
            devName.setTypeface(typeface);
            designerName.setTypeface(typeface);
        } catch (Exception e) {
            //
        }

        versionName.setText(appVersion);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
