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

package hr.mk.wpmagazine.android.component.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Calendar;
import java.util.Date;

import hr.mk.wpmagazine.Utils.PreferenceUtils;
import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.activities.FeedViewerActivity;
import hr.mk.wpmagazine.android.component.broadcast.GcmBroadcastReceiver;
import hr.mk.wpmagazine.client.MyRestTemplate;
import hr.mk.wpmagazine.helper.RealmHelper;
import hr.mk.wpmagazine.model.ObjectParcel;
import hr.mk.wpmagazine.model.WP.WPPost;
import io.realm.RealmResults;

/**
 * Created by Mur0 on 10/30/2014.
 */
public class GcmIntentService extends IntentService {

    private static final String TAG = TagFactoryUtils.getTag(GcmIntentService.class);

    public GcmIntentService() {
        super(TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        final Bundle extras = intent.getExtras();
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(GcmIntentService.this);
        final MyRestTemplate restTemplate = MyRestTemplate.getInstance(getResources().getString(R.string.baseUrl));
        final String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {  // From Google ====>                           has effect of unparcelling Bundle
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                final String title = getResources().getString(R.string.app_name);
                int notificationId = 0;
                if (extras.containsKey("message")) {
                    String msg = extras.getString("message");
                    if (PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_ON))
                        sendNotification(title, msg, null);
                } else if (extras.containsKey("update")) {
                    Log.d(TAG, "Update received... and processing");
                    try {
                        final String link = extras.getString("update");
                        RealmResults<WPPost> realmResults = RealmHelper.queryEqualTo(GcmIntentService.this, WPPost.class, "link", link);//unfortunately i don't get updates IDs:(
                        if (realmResults != null && !realmResults.isEmpty()) {
                            final WPPost wpPost = restTemplate.getForObject("/wp-json/posts/{ID}", WPPost.class, realmResults.get(0).getID());
                            if (wpPost.getID() == realmResults.get(0).getID()) {// should always be true, but just for sanity check
                                wpPost.setFavorite(realmResults.get(0).isFavorite());
                                RealmHelper.realmWrite(GcmIntentService.this, wpPost);
                                Log.d(TAG, "Update processed!");
                            }
                        }
                    } catch (Exception e) {
                        //
                    }
                } else {
                    try {
                        final String id = extras.getString("new_post");
                        Log.d(TAG, String.format("new post notification received ID: %s", id));
                        notificationId = Integer.valueOf(id);
                        final WPPost wpPost = restTemplate.getForObject("/wp-json/posts/{ID}", WPPost.class, notificationId);
                        RealmHelper.realmWrite(GcmIntentService.this, wpPost);
                        if (wpPost.getTitle() != null && wpPost.getDate() != null && verifyIfToOld(wpPost.getDate()) && PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_ON)) {
                            sendNotification(title, Html.fromHtml(wpPost.getTitle()), wpPost);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "", e);
                    }
                }
                Log.d(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
        // Release the wake lock provided by the WakefulBroadcastReceiver.
    }

    private boolean verifyIfToOld(final Date date) {
        final Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        final Calendar calIn = Calendar.getInstance();
        calIn.setTimeInMillis(date.getTime());
        final int elapseDays = now.get(Calendar.DAY_OF_YEAR) - calIn.get(Calendar.DAY_OF_YEAR);
        return now.get(Calendar.YEAR) == calIn.get(Calendar.YEAR) && elapseDays <= getResources().getInteger(R.integer.notification_validity_days);
    }

    private void sendNotification(CharSequence title, CharSequence msg, WPPost wpPost) {
        Log.d(TAG, String.format("notification message: %s notification ID: %d", msg, wpPost == null ? 0 : wpPost.getID()));
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        int drawableRes = (Build.VERSION.SDK_INT > 21) ? R.drawable.ic_launcher_notif : R.drawable.ic_launcher;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setSmallIcon(drawableRes)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setLights(Color.BLUE, 500, 500)
                        .setContentText(msg);
        int defaults = 0;
        if (PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_SOUND)) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        if (PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_VIBRATION))
            defaults |= Notification.DEFAULT_VIBRATE;

        builder.setDefaults(defaults);

        if (wpPost != null && wpPost.getID() > 0) {
            final Intent intent = new Intent(this, FeedViewerActivity.class);
            String cat = getResources().getString(R.string.news);
            if (wpPost.getTerms() != null && wpPost.getTerms().getCategory() != null && !wpPost.getTerms().getCategory().isEmpty())
                cat = wpPost.getTerms().getCategory().get(0).getName();
            final ObjectParcel objectParcel = new ObjectParcel(wpPost.getID(), cat, getResources().getColor(R.color.primaryColor), getResources().getColor(R.color.colorPrimaryDark));
            objectParcel.fromNotification = true;
            intent.putExtra(FeedViewerActivity.OBJECT_KEY, objectParcel)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, wpPost.getID(), intent
                    , PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(contentIntent);
            mNotificationManager.notify(wpPost.getID(), builder.build());
        } else
            mNotificationManager.notify(0, builder.build());
    }
}