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

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Iterator;

import de.greenrobot.event.EventBus;
import hr.mk.wpmagazine.Utils.ConnectivityUtils;
import hr.mk.wpmagazine.Utils.PreferenceUtils;
import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.client.MyRestTemplate;
import hr.mk.wpmagazine.helper.RealmHelper;
import hr.mk.wpmagazine.model.BusEvents;
import hr.mk.wpmagazine.model.WP.WPPost;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Mur0 on 3/22/2015.
 */
public class UpdaterService extends Service {

    public static final String COMMAND = "Service_Command";
    public static final int GET_ALL_POST = 1;
    public static final int UPDATE_DATA_BASE = 2;
    public static final int UPDATE_DELAY = 3;
    private static final String TAG = TagFactoryUtils.getTag(UpdaterService.class);
    private static final long TWO_HOURS = 3600000 * 2/*So every loop is now 2 hours as push notification will take care of articles updates*/;
    private static final long TEN_MIN = 600000;
    private static long delay = 0;

    private ServiceHandlerThread serviceHandlerThread;
    private MyRestTemplate restTemplate;
    private EventBus eventBus;

    private volatile boolean isInitAllPostFlag;
    private boolean isDestroy;
    private boolean isUpdatingFlag;
    private boolean isWaitingInternetFlag;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " Service Created ");
        serviceHandlerThread = new ServiceHandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND, this);
        restTemplate = MyRestTemplate.getInstance(getResources().getString(R.string.baseUrl));
        eventBus = EventBus.getDefault();
    }

    private void postCommand(int command) {
        postCommand(command, 0);
    }

    private void postCommand(int command, long delay) {
        serviceHandlerThread.handler.sendEmptyMessageDelayed(command, delay);
    }

    private void cancelCommand(int command) {
        serviceHandlerThread.handler.removeMessages(command);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, " service onStartCommand, startId: " + startId);
        if (intent.getExtras() != null) {
            final int command = intent.getExtras().getInt(COMMAND, -1);
            Log.d(TAG, " service  onStartCommand, command " + command);
            postCommand(command);
        }

        return START_NOT_STICKY;
    }

    private void updateDataBase() {
        Log.d(TAG, "start checking for update ");
        isUpdatingFlag = true;
        boolean success = false;
        boolean wasUpDate = false;
        Realm realm = null;

        try {
            if (!ConnectivityUtils.isOnline(this)) {
                isWaitingInternetFlag = true; // wait for internetFlag to automatically before delay in case of reconnection
                delay = TEN_MIN;
                Log.d(TAG, "no internet break...");
            } else {
                final int maxPost = getResources().getInteger(R.integer.max_update_post);
                final String postType = getResources().getString(R.string.post_type);
                final WPPost[] wpPost = restTemplate.getForObject(String.format("/wp-json/posts?type[]=%s&filter[posts_per_page]=%d&filter[order]=DSC", postType, maxPost), WPPost[].class);
                Log.d(TAG, String.format("fetched %d articles", wpPost == null ? 0 : wpPost.length));
                int minID = 0;
                for (WPPost wp : wpPost) {// if wpPost ==null i will catch the exception
                    if (minID == 0)
                        minID = wp.getID();
                    else if (minID > wp.getID())
                        minID = wp.getID();
                }
                Log.d(TAG, String.format("min local ID: %d ", minID));
                realm = Realm.getInstance(this);
                RealmResults realmResults = realm.where(WPPost.class).greaterThanOrEqualTo("ID", minID).findAll();
                RealmList<WPPost> wpPostIn = new RealmList<>();
                wpPostIn.addAll(Arrays.asList(wpPost));
                for (WPPost wpLocale : (Iterable<WPPost>) realmResults) { // i cant use the contain method with realm object so i have to do this :(
                    final Iterator<WPPost> iterator = wpPostIn.iterator();
                    while (iterator.hasNext()) {
                        final WPPost wpIn = iterator.next();
                        if (wpLocale.getID() == wpIn.getID()) {
                            if (wpLocale.getModified().equals(wpIn.getModified())) {// no change with the locale one so remove it
                                Log.d(TAG, String.format("removing existing article:  %s ", wpIn.getTitle() == null ? "No name" : wpIn.getTitle()));
                                iterator.remove();
                            } else
                                wpIn.setFavorite(wpLocale.isFavorite());
                        }
                    }
                }
                Log.d(TAG, String.format("articles size to update?  %d", wpPostIn.size()));
                if (!wpPostIn.isEmpty()) {
                    realm.beginTransaction(); // finally write the update into the realm
                    realm.copyToRealmOrUpdate(wpPostIn);
                    realm.commitTransaction();
                    wasUpDate = true;
                }
                success = true;
                delay = TWO_HOURS;// two hours ;)
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
            delay = TEN_MIN;
        } finally {

            if (realm != null)
                realm.close();

            if (success && wasUpDate)
                eventBus.post(new BusEvents(null, BusEvents.EvenType.DATA_BASE_UPDATED));
            else if (success)
                eventBus.post(new BusEvents(null, BusEvents.EvenType.DATA_BASE_IS_UP_TO_DATE));
            else
                eventBus.post(new BusEvents(null, BusEvents.EvenType.DATA_BASE_UPDATED_FAIL));

            postCommand(UPDATE_DATA_BASE, delay); // try to do that again in x=delay  time

            isUpdatingFlag = false;

            Log.d(TAG, "update done success? " + success);

        }
    }

    private void getAlLPost() {
        isInitAllPostFlag = true;
        Log.d(TAG, "start getAlLPost ");
        eventBus.post(new BusEvents(null, BusEvents.EvenType.GET_DATA_START));
        boolean success = false;
        long startTime = System.currentTimeMillis();

        try {
            final int maxPostInit = getResources().getInteger(R.integer.max_init_post);
            final String postType = getResources().getString(R.string.post_type);
            final WPPost[] wpPost = restTemplate.getForObject(String.format("/wp-json/posts?type[]=%s&filter[posts_per_page]=%d&filter[order]=DSC", postType, maxPostInit), WPPost[].class);
            Log.d(TAG, "Fetch all data from server in : " + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
            startTime = System.currentTimeMillis();
            success = RealmHelper.realmWrite(UpdaterService.this, Arrays.asList(wpPost));
            Log.d(TAG, "Stored all data in data base in : " + String.valueOf(System.currentTimeMillis() - startTime) + "ms" + " data size: " + wpPost.length);

        } catch (Exception e) {
            Log.e(TAG, "", e);
            eventBus.post(new BusEvents(null, BusEvents.EvenType.GET_DATA_FAIL));
        } finally {
            if (success) {
                eventBus.post(new BusEvents(null, BusEvents.EvenType.GET_DATA_DONE));
                PreferenceUtils.putBoolean(UpdaterService.this, PreferenceUtils.Preferences.DATA_BASE_LOADED, true);
            }
            isInitAllPostFlag = false;
            Log.d(TAG, " getAlLPost isSuccess? " + success);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        if (serviceHandlerThread != null) {
            serviceHandlerThread.quit();
        }
        Log.d(TAG, " Service Destroyed ");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onEventMainThread(BusEvents event) {
        if (event.evenType == BusEvents.EvenType.CONNECTIVITY && ConnectivityUtils.isOnline(this)
                && isWaitingInternetFlag && !isUpdatingFlag) {// start automatically if isWaitingInternetFlag and internet has returned
            postCommand(UPDATE_DATA_BASE);
            isWaitingInternetFlag = false;
        }
    }

    private static class ServiceHandler extends android.os.Handler {

        private final WeakReference<UpdaterService> weakReference;

        public ServiceHandler(Looper looper, WeakReference<UpdaterService> weakReference) {
            super(looper);
            this.weakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {

            final UpdaterService updaterService = weakReference.get();
            if (updaterService == null) return;

            switch (msg.what) {
                case GET_ALL_POST:
                    if (updaterService.isInitAllPostFlag)
                        return;
                    updaterService.getAlLPost();
                    break;
                case UPDATE_DATA_BASE:
                    if (updaterService.isUpdatingFlag)
                        return;
                    updaterService.cancelCommand(UPDATE_DATA_BASE);
                    updaterService.updateDataBase();
                    break;
                case UPDATE_DELAY:
                    updaterService.cancelCommand(UPDATE_DATA_BASE);
                    updaterService.postCommand(UPDATE_DATA_BASE, TWO_HOURS);
                    break;
                default:
                    Log.e(TAG, String.format("Invalid command received: %d", msg.what));
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private class ServiceHandlerThread extends HandlerThread {

        private final ServiceHandler handler;

        public ServiceHandlerThread(String name, int priority, UpdaterService updaterService) {
            super(name, priority);
            start();
            handler = new ServiceHandler(getLooper(), new WeakReference<UpdaterService>(updaterService));
        }
    }
}
