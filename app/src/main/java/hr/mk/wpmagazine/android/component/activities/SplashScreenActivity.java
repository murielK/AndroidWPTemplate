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

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;
import hr.mk.wpmagazine.Utils.ConnectivityUtils;
import hr.mk.wpmagazine.Utils.PreferenceUtils;
import hr.mk.wpmagazine.Utils.TypeFaceUtils;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.services.UpdaterService;
import hr.mk.wpmagazine.model.BusEvents;

/**
 * Created by Mur0 on 4/2/2015.
 */
public class SplashScreenActivity extends AbsBaseActivity {

    @InjectView(R.id.progressBarSplash)
    ProgressBar progressBar;
    @InjectView(R.id.layoutRetry)
    LinearLayout linearLayout;
    @InjectView(R.id.textViewSplash)
    TextView textView;
    @InjectView(R.id.imageViewSplash)
    ImageView imageView;

    AnimationDrawable animation;

    private boolean wasPaused;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.splash_screen_activity);
        setTint(getResources().getColor(R.color.splash_status_color));
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (checkDataLoaded())
            return;

        PreferenceUtils.putBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_ON, true);
        PreferenceUtils.putBoolean(this, PreferenceUtils.Preferences.NOTIFICATION_SOUND, true);
        PreferenceUtils.putBoolean(this, PreferenceUtils.Preferences.DOWNLOAD_IMAGES, true);

        final Typeface typeface = TypeFaceUtils.getTypeFace(this, "OpenSans-Regular.ttf");
        textView.setTypeface(typeface);

        if (getResources().getBoolean(R.bool.is_custom_loading)) {
            progressBar.setVisibility(View.GONE);
            imageView.setBackgroundResource(R.drawable.animated_drawable);
            animation = (AnimationDrawable) imageView.getBackground();
            animation.start();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            imageView.setBackground(getResources().getDrawable(R.drawable.splash));
        else
            imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.splash));

        initDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wasPaused)
            checkDataLoaded();
    }

    private boolean checkDataLoaded() {
        if (PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.DATA_BASE_LOADED)) {
            startFeedActivity(false);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        wasPaused = true;
    }

    private void initDatabase() {
        if (!ConnectivityUtils.isOnline(this)) {
            showRetry();
            toast(getResources().getString(R.string.no_internet_connection_retry));
            return;
        }
        sendServiceCommand(UpdaterService.GET_ALL_POST);
    }

    @OnClick(R.id.layoutRetry)
    public void onRetryClick(View v) {
        hideRetry();
        registerGCM();
        initDatabase();
    }

    private void startFeedActivity(boolean startUpdateDelay) {
        final Intent intent;
        if ("post".equals(getResources().getString(R.string.post_type))) {
            intent = new Intent(this, FeedsActivity.class);
        } else
            intent = new Intent(this, GalleryActivity.class); //For now i only support this 2 types

        if (startUpdateDelay)
            intent.putExtra(START_UPDATE_DELAY, true);

        startActivity(intent);
    }


    @Override
    public void onEventMainThread(BusEvents event) {
        super.onEventMainThread(event);
        switch (event.evenType) {
            case GET_DATA_START:
                //
                break;
            case GET_DATA_DONE:
                startFeedActivity(true);
                finish();
                break;
            case GET_DATA_FAIL:
                showRetry();
                toast(getResources().getString(R.string.init_fail_retry));
                break;
        }
    }

    private void hideRetry() {

        linearLayout.animate().cancel();
        linearLayout.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                linearLayout.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                linearLayout.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        linearLayout.animate().alpha(0).setDuration(300).start();

        if (!getResources().getBoolean(R.bool.is_custom_loading)) {
            progressBar.animate().cancel();
            progressBar.animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            progressBar.animate().alpha(1).setStartDelay(100).setDuration(400).start();
        } else {
            if (animation != null)
                animation.start();
        }

    }

    private void showRetry() {
        if (!getResources().getBoolean(R.bool.is_custom_loading)) {
            progressBar.animate().cancel();
            progressBar.animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            progressBar.animate().alpha(0).setDuration(400).start();

        } else {
            if (animation != null)
                animation.stop();
        }

        linearLayout.animate().cancel();
        linearLayout.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                linearLayout.setEnabled(true);
                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        linearLayout.animate().alpha(1).setStartDelay(100).setDuration(300).start();
    }
}
