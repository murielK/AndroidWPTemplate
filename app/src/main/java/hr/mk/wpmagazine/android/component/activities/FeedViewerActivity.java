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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import butterknife.OnClick;
import hr.mk.wpmagazine.Utils.ExecutorUtils;
import hr.mk.wpmagazine.Utils.ImageGetterUtils;
import hr.mk.wpmagazine.Utils.PreferenceUtils;
import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.Utils.TypeFaceUtils;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.helper.RealmHelper;
import hr.mk.wpmagazine.helper.ShowDateHelper;
import hr.mk.wpmagazine.model.ObjectParcel;
import hr.mk.wpmagazine.model.WP.WPPost;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Mur0 on 3/27/2015.
 */
public class FeedViewerActivity extends AbsBaseActivity implements ObservableScrollViewCallbacks {

    public static final String OBJECT_KEY = "OBJECT_KEY";
    private static final String TAG = TagFactoryUtils.getTag(FeedViewerActivity.class);
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = true;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.image)
    ImageView imageView;
    @InjectView(R.id.overlay)
    View overlayView;
    @InjectView(R.id.scroll)
    ObservableScrollView scrollView;
    @InjectView(R.id.title)
    TextView titleView;
    @InjectView(R.id.contentTitle)
    TextView titleContent;
    @InjectView(R.id.content)
    TextView content;
    @InjectView(R.id.contentDate)
    TextView contentDate;
    @InjectView(R.id.contentAuthor)
    TextView author;
    @InjectView(R.id.textViewReadMore)
    TextView readMore;
    @InjectView(R.id.fab)
    FloatingActionButton fab;

    private ImageGetterUtils imageGetterUtils;
    private ObjectParcel objectParcel;
    private Realm realm;
    private RealmResults<WPPost> realmResults;
    private ShowDateHelper showDateHelper;

    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private int toolbarColor;
    private boolean mFabIsShown;
    private int colorDark;

    @OnClick(R.id.textViewReadMore)
    public void onReadMoreClick(View v) {
        if (realmResults == null || realmResults.isEmpty())
            return;
        final Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.COLOR_KEY, toolbarColor);
        intent.putExtra(WebViewActivity.COLOR_KEY_DARK, colorDark);
        intent.putExtra(WebViewActivity.LINK_KEY, realmResults.get(0).getLink());
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(OBJECT_KEY, objectParcel);
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_feed_view);
        showDateHelper = new ShowDateHelper(this);
        realm = Realm.getInstance(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        imageGetterUtils = new ImageGetterUtils(content, PreferenceUtils.getBoolean(this, PreferenceUtils.Preferences.DOWNLOAD_IMAGES));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(null);

        final Typeface typeface = TypeFaceUtils.getTypeFace(this, "OpenSans-Regular.ttf");
        final Typeface typefaceSemiBold = TypeFaceUtils.getTypeFace(this, "OpenSans-Semibold.ttf");
        if (typeface != null) {
            content.setTypeface(typeface);
            contentDate.setTypeface(typeface);
        }
        if (typefaceSemiBold != null) {
            titleContent.setTypeface(typefaceSemiBold);
            readMore.setTypeface(typefaceSemiBold);
        }


        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);

        mActionBarSize = getActionBarSize();
        if (!TOOLBAR_IS_STICKY) {
            toolbar.setBackgroundColor(Color.TRANSPARENT);
        }

        scrollView.setScrollViewCallbacks(this);

        fab.setOnClickListener(new View.OnClickListener() { //set fab
            @Override
            public void onClick(View v) {
                if (realmResults != null) {
                    final int ID = realmResults.get(0).getID();
                    final boolean fav = realmResults.get(0).isFavorite();
                    ExecutorUtils.getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            final boolean ok = RealmHelper.writeFavorite(FeedViewerActivity.this, ID, !fav);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateFapFav();
                                    if (ok)
                                        toast(getResources().getString(R.string.fav_added));
                                    else
                                        toast(getResources().getString(R.string.fav_delete));
                                }
                            });
                        }
                    });
                }
            }
        });
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        fab.setScaleX(0);
        fab.setScaleY(0);

        executeAtOnGlobalLayoutListener(scrollView, new Runnable() {
            @Override
            public void run() {
                //scrollView.scrollTo(0,mActionBarSize);

                // If you'd like to start from scrollY == 0, don't write like this:
                //scrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
                //scrollView.scrollTo(0, 1);
                //scrollView.scrollTo(0, 0);
            }
        });

        executeAtOnGlobalLayoutListener(content, new Runnable() {
            @Override
            public void run() {
                setContentMinHeight();
            }
        });

        setContent(savedInstanceState, getIntent());
    }


    private void setContentMinHeight() {
        int minTextHeight;
        final int decorViewHeight = getWindow().getDecorView().findViewById(android.R.id.content).getHeight();
        Log.d(TAG, String.format("decorView height: %d", decorViewHeight));
        if (mFlexibleSpaceImageHeight > (decorViewHeight - mFlexibleSpaceImageHeight)) {
            minTextHeight = mFlexibleSpaceImageHeight - (decorViewHeight - mFlexibleSpaceImageHeight);
        } else
            minTextHeight = decorViewHeight - mFlexibleSpaceImageHeight;
        Log.d(TAG, String.format("minimum textView height: %d", minTextHeight));
        content.setMinHeight(minTextHeight);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "On new intent called");
        setContent(null, intent);
    }

    private void updateFapFav() {
        if (realmResults != null) {
            if (realmResults.get(0).isFavorite())
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_true));
            else
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_false));
        }

    }

    private void setToolbarColor(int primaryColor, int colorDark) {
        toolbarColor = primaryColor;
        this.colorDark = colorDark;
        fab.setColorNormal(toolbarColor);
        fab.setColorPressed(colorDark);
        overlayView.setBackgroundColor(toolbarColor);
        setTint(colorDark);
    }

    private void setContent(Bundle savedInstanceState, Intent intent) {

        if (savedInstanceState != null) {
            objectParcel = savedInstanceState.getParcelable(OBJECT_KEY);
        } else if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(OBJECT_KEY)) {
            objectParcel = intent.getParcelableExtra(OBJECT_KEY);
        }

        if (objectParcel == null) {
            onBackPressed();
            return;
        }

        titleView.setText(objectParcel.category);

        if (objectParcel.color != 0) {
            setToolbarColor(objectParcel.color, objectParcel.colorDark);
        } else {
            setToolbarColor(getResources().getColor(R.color.primaryColor), getResources().getColor(R.color.colorPrimaryDark));
        }

        ExecutorUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    RealmHelper.queryEqualTo(FeedViewerActivity.this, WPPost.class, "ID", objectParcel.ID);
                } catch (Exception e) {
                    Log.d(TAG, "", e);
                } finally {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                realmResults = RealmHelper.queryEqualTo(realm, WPPost.class, "ID", objectParcel.ID);
                                final WPPost wpPost = realmResults.get(0);

                                titleContent.setText(Html.fromHtml(wpPost.getTitle()));

                                final String contentString = wpPost.getContent();
                                content.setText(Html.fromHtml(contentString, imageGetterUtils, null));

                                LinkMovementExCaught linkMovementExCaught = new LinkMovementExCaught();
                                linkMovementExCaught.setListener(new LinkMovementExCaught.LinkMovementExListener() {
                                    @Override
                                    public void onException(Exception e) {
                                        toast(getResources().getString(R.string.could_not_load_link));
                                    }
                                });
                                content.setMovementMethod(linkMovementExCaught);

                                contentDate.setText(showDateHelper.getFormattedDate(wpPost.getDate()));

                                if (wpPost.getAuthor() == null || wpPost.getAuthor().getName() == null)
                                    author.setVisibility(View.GONE);
                                else
                                    author.setText(wpPost.getAuthor().getName());
                                updateFapFav();

                                try {
                                    final String imgLink = realmResults.get(0).getFeatured_image().getSource();
                                    Picasso.with(FeedViewerActivity.this)
                                            .load(imgLink)
                                            .fit()
                                            .config(Bitmap.Config.RGB_565)
                                            .centerCrop()
                                            .error(R.drawable.img_holder)
                                            .into(imageView);
                                } catch (Exception e) {
                                    if (imageView != null)
                                        imageView.setImageDrawable(FeedViewerActivity.this.getResources().getDrawable(R.drawable.img_holder));
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "", e);
                                //TODO please handle this error message
                            }

                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (objectParcel.fromNotification) {
            Intent intent = new Intent(this, FeedsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image

        final float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        final int minOverlayTransitionY = mActionBarSize - overlayView.getHeight();
        overlayView.setTranslationY(ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        imageView.setTranslationY(ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        overlayView.setAlpha(ScrollUtils.getFloat((float) scrollY / flexibleRange / 1.2f, 0, 1));
        titleView.setAlpha(ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));
        // Scale title text
        final float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        titleView.setPivotX(0);
        titleView.setPivotY(0);
        titleView.setScaleX(scale);
        titleView.setScaleY(scale);

        final int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - titleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
        }
        titleView.setTranslationY(titleTranslationY);

        // Translate FAB
        final int maxFabTranslationY = mFlexibleSpaceImageHeight - fab.getHeight() / 2;
        final float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - fab.getHeight() / 2,
                mActionBarSize - fab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
            lp.leftMargin = overlayView.getWidth() - mFabMargin - fab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            fab.requestLayout();
        } else {
            fab.setTranslationX(overlayView.getWidth() - mFabMargin - fab.getWidth());
            fab.setTranslationY(fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }

        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, toolbarColor));

            } else {
                toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, toolbarColor));
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight - mActionBarSize) {
                toolbar.setTranslationY(0);
            } else {
                toolbar.setTranslationY(titleTranslationY);
            }
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void showFab() {
        if (!mFabIsShown) {
            fab.animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                    fab.setVisibility(View.VISIBLE);
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

            fab.animate().cancel();
            fab.animate().scaleX(1).scaleY(1).setDuration(400).start();
            mFabIsShown = true;
            fab.setEnabled(true);
        }
    }

    private void hideFab() {
        if (mFabIsShown) {

            fab.animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    fab.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            fab.animate().cancel();
            fab.animate().scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
            fab.setEnabled(false);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
        realm = null;// "Force" GC, pretty much useless ;)

        if (realmResults != null) {
            realmResults = null;
        }

        if (imageGetterUtils != null)
            imageGetterUtils.recycleBitmaps();
    }

    private static class LinkMovementExCaught extends LinkMovementMethod {


        private static LinkMovementExCaught instance;
        private LinkMovementExListener listener;

        public static LinkMovementExCaught getInstace() {
            if (instance == null)
                instance = new LinkMovementExCaught();
            return instance;
        }

        public void setListener(LinkMovementExListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Exception e) {
                if (listener != null)
                    listener.onException(e);
                return true;
            }
        }

        public interface LinkMovementExListener {

            void onException(Exception e);
        }
    }


}
