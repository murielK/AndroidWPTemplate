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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import hr.mk.wpmagazine.Utils.ConnectivityUtils;
import hr.mk.wpmagazine.Utils.ExecutorUtils;
import hr.mk.wpmagazine.Utils.PreferenceUtils;
import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.services.UpdaterService;
import hr.mk.wpmagazine.android.component.ui.widget.ObsABDrawerToggle;
import hr.mk.wpmagazine.helper.PopUpMessageHelper;
import hr.mk.wpmagazine.model.BusEvents;

/**
 * Created by Mur0 on 3/15/2015.
 */
public class AbsBaseActivity extends AppCompatActivity {

    public static final String START_UPDATE_DELAY = "START_UPDATE_DELAY";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = TagFactoryUtils.getTag(AbsBaseActivity.class);

    private static final Object lock = new Object();
    protected PopUpMessageHelper popUpMessageHelper;
    protected EventBus eventBus;
    String regID;
    GoogleCloudMessaging gcm;
    private SystemBarTintManager tintManager;

    public static void executeAtOnGlobalLayoutListener(final View v, final Runnable runnable) {
        final ViewTreeObserver viewTreeObserver = v.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16)
                    v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                runnable.run();
            }
        });
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    protected static void toggleDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (Build.VERSION.SDK_INT >= 21)
            postponeEnterTransition();

    }

    protected void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (Build.VERSION.SDK_INT >= 21)
                            startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    private void initAnimation() {
        if (Build.VERSION.SDK_INT >= 21) {
            //To enable window content transitions in your code instead, call the Window.requestFeature() method:
            getWindow().requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS);
            Transition ts_enter = new ChangeImageTransform();  //Slide(); //Explode();
            Transition ts_exit = new ChangeTransform();

            ts_enter.setDuration(400);
            ts_exit.setDuration(400);
        /*
        If you have set an enter transition for the second activity,
        the transition is also activated when the activity starts.
        */
            getWindow().setEnterTransition(ts_enter);
            getWindow().setExitTransition(ts_exit);
        }
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        //initAnimation();
        super.onCreate(saveInstanceState);
        if (!(this instanceof SplashScreenActivity)) // to avoid that on the splash screen
            registerGCM();

        popUpMessageHelper = new PopUpMessageHelper(this);
        eventBus = EventBus.getDefault();
        tintManager = new SystemBarTintManager(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.inject(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        eventBus.register(this);
        if (!(this instanceof SplashScreenActivity))
            checkPlayServices();
        try {
            if (ConnectivityUtils.isOnline(this))
                popUpMessageHelper.hide();
            else
                popUpMessageHelper.show(R.drawable.ic_err_connection, getResources().getString(R.string.no_internet_connection));
        } catch (Exception e) {
            Log.d(TAG, "", e);
        }

    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (popUpMessageHelper.isShowing())
                popUpMessageHelper.hide();
        } catch (Exception e) {
            Log.d(TAG, "", e);
        }
        eventBus.unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public void onEventMainThread(BusEvents event) {
        if (event.evenType == BusEvents.EvenType.CONNECTIVITY) {
            if (ConnectivityUtils.isOnline(this))
                popUpMessageHelper.hide();
            else
                popUpMessageHelper.show(R.drawable.ic_err_connection, getResources().getString(R.string.no_internet_connection));
        }

    }

    protected void setTint(int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(color);
            getWindow().setNavigationBarColor(color);
        } else {
            if (!tintManager.isStatusBarTintEnabled())
                tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(color);
            if (!tintManager.isNavBarTintEnabled())
                tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintColor(color);
        }

    }

    protected void toast(final String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.d(TAG, "", e);
        }
    }

    public void sendServiceCommand(int command) {
        Intent intent = new Intent(this, UpdaterService.class);
        intent.putExtra(UpdaterService.COMMAND, command);
        startService(intent);
    }

    protected void registerGCM() {
        if (checkPlayServices()) {
            Log.d(TAG, "getting gcm instance");
            gcm = GoogleCloudMessaging.getInstance(this);
            regID = getRegistrationId(this);
            Log.d(TAG, "regId: " + regID);
            if (regID.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                toast("This device is not supported : Google Play Service missing!");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        String registrationId = PreferenceUtils.getString(this, PreferenceUtils.Preferences.PROPERTY_REG_ID);
        if (registrationId == null || registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = PreferenceUtils.getInt(this, PreferenceUtils.Preferences.PROPERTY_APP_VERSION);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        Log.i(TAG, "Registration  found.");
        return registrationId;
    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        PreferenceUtils.putString(AbsBaseActivity.this, PreferenceUtils.Preferences.PROPERTY_REG_ID, regId);
        PreferenceUtils.putInt(AbsBaseActivity.this, PreferenceUtils.Preferences.PROPERTY_APP_VERSION, appVersion);
    }

    private void registerInBackground() {
        ExecutorUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (!getRegistrationId(AbsBaseActivity.this).isEmpty())
                        return;

                    String msg = "";
                    Log.d(TAG, "Trying to registered this device to GCM");
                    try {
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(AbsBaseActivity.this);
                        }
                        final InstanceID instanceID = InstanceID.getInstance(AbsBaseActivity.this);
                        final String projectId = AbsBaseActivity.this.getResources().getString(R.string.project_id);
                        Log.d(TAG, String.format("GCM project ID: %s", projectId));
                        regID = instanceID.getToken(projectId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                        Log.d(TAG, String.format("Device registered, registration ID= %s", regID));
                        // You should send the registration ID to your server over HTTP,
                        // so it can use GCM/HTTP or CCS to send messages to your app.
                        // The request to your server should be authenticated if your app
                        // is using accounts.

                        // For this demo: we don't need to send it because the device
                        // will send upstream messages to a server that echo back the
                        // message using the 'from' address in the message.

                        // Persist the regID - no need to register again.
                        Log.d(TAG, "Trying to reg to server for Push notifications");
                        SendRegIDTOServer(regID);
                        storeRegistrationId(AbsBaseActivity.this, regID);
                        Log.d(TAG, "registration done");
                    } catch (Exception e) {
                        Log.d(TAG, msg, e);
                    }
                }
            }
        });
    }

    public void SendRegIDTOServer(final String regid) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(getResources().getString(R.string.baseUrl) + "?regId=" + regid);
        httpclient.execute(httppost);

    }

    protected void startServiceUpdater() {
        if (getIntent().getExtras() == null || !getIntent().getExtras().getBoolean(START_UPDATE_DELAY))
            sendServiceCommand(UpdaterService.UPDATE_DATA_BASE);
        else {
            sendServiceCommand(UpdaterService.UPDATE_DELAY);
            Log.d(TAG, "DADA UPDATE WILL START WITH DELAY AS REQUESTED");
        }
    }

    protected ObsABDrawerToggle setUpDrawerToggle(DrawerLayout drawerLayout) {
        ObsABDrawerToggle drawerToggle = new ObsABDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        return drawerToggle;
    }

    protected void seUpDefaultToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setTitleTextColor(Color.WHITE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }


}
