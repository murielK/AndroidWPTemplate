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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.android.component.ui.fragments.FeedDrawerFragment;
import hr.mk.wpmagazine.android.component.ui.fragments.FeedsFragment;
import hr.mk.wpmagazine.android.component.ui.widget.ObsABDrawerToggle;

/**
 * Created by Mur0 on 3/15/2015.
 */
public class FeedsActivity extends AbsBaseActivity implements ObservableScrollViewCallbacks {

    public static final String ARG_SCROLL_Y = "ARG_SCROLL_Y";
    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";
    private static final String TAG = TagFactoryUtils.getTag(FeedsActivity.class);
    private static final String CURRENT_POSITION = "CURRENT_POSITION";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.pager)
    ViewPager viewPager;
    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout slidingTabLayout;
    @InjectView(R.id.header)
    LinearLayout header;
    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    private PageAdapter pageAdapter;

    private int mBaseTranslationY;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_feeds);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, viewPager.getCurrentItem());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        seUpDefaultToolbar(toolbar);
        ObsABDrawerToggle drawerToggle = setUpDrawerToggle(drawerLayout);
        drawerToggle.setObservableDrawerListener(new ObsABDrawerToggle.ObservableDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getCurrentScrollable().setScrollViewCallbacks(null);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getCurrentScrollable().setScrollViewCallbacks(FeedsActivity.this);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        ViewCompat.setElevation(header, getResources().getDimension(R.dimen.elevation));

        pageAdapter = new PageAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pageAdapter);

        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //setToolbarOnPageChange();

            }

            @Override
            public void onPageSelected(int position) {
                setToolbarOnPageChange();
                setColors(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setColors(0);

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            FeedDrawerFragment fragment = new FeedDrawerFragment();
            fm.beginTransaction().replace(R.id.drawer, fragment, fragment.getFragmentTag()).commit();
        } else if (savedInstanceState.containsKey(CURRENT_POSITION)) {
            final int restoredPos = savedInstanceState.getInt(CURRENT_POSITION);
            viewPager.setCurrentItem(restoredPos);
            setColors(restoredPos);
        }

        startServiceUpdater();

    }


    private void setColors(int position) {
        try {
            toolbar.setBackgroundColor(pageAdapter.getColor(position));
            slidingTabLayout.setBackgroundColor(pageAdapter.getColor(position));
            setTint(pageAdapter.getColorDark(position));
        } catch (Exception e) {
            Log.d(TAG, "", e);
        }
    }

    private Scrollable getCurrentScrollable() {
        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        return (Scrollable) view.findViewById(R.id.scroll);
    }

    private void adjustToolbar(ScrollState scrollState, View view) {
        int toolbarHeight = toolbar.getHeight();
        final Scrollable scrollView = (Scrollable) view.findViewById(R.id.scroll);
        if (scrollView == null) {
            return;
        }
        int scrollY = scrollView.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }

        } else {
            // Even if ScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (toolbarIsShown() || toolbarIsHidden()) {
                // Toolbar is completely moved, so just keep its state
                // and propagate it to other pages
                setToolbarOnPageChange();
            } else {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
            }
        }
    }

    private Fragment getCurrentFragment() {
        return pageAdapter.getItemAt(viewPager.getCurrentItem());
    }

    private boolean toolbarIsShown() {
        return header.getTranslationY() == 0;
    }

    private boolean toolbarIsHidden() {
        return header.getTranslationY() == -toolbar.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = header.getTranslationY();
        if (headerTranslationY != 0) {
            header.animate().cancel();
            header.animate().translationY(0).setDuration(200).start();
        }
    }

    private void hideToolbar() {
        float headerTranslationY = header.getTranslationY();
        int toolbarHeight = toolbar.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            header.animate().cancel();
            header.animate().translationY(-toolbarHeight).setDuration(200).start();
        }
    }

    private void setToolbarOnPageChange() {
        final Scrollable scrollView = getCurrentScrollable();
        final int currentScrollY = scrollView.getCurrentScrollY();
        final int toolbarHeight = toolbar.getHeight();
        scrollView.scrollVerticallyTo(currentScrollY);
        Log.d(TAG, "toolbarHeight: " + toolbarHeight + "currentScrollY: " + currentScrollY);
        if (currentScrollY < toolbarHeight) // if you want the toolbar to show when the list show first 1 item
            showToolbar();
//        if (currentScrollY < toolbarHeight && toolbarIsHidden())// if  you want the list to always goes up to stick with tab
//            scrollView.scrollVerticallyTo(toolbarHeight);


    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = toolbar.getHeight();
            float currentHeaderTranslationY = header.getTranslationY();
            if (firstScroll) {
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            header.animate().cancel();
            header.setTranslationY(headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return;
        }
        View view = fragment.getView();
        if (view == null) {
            return;
        }

        // ObservableXxxViews have same API
        // but currently they don't have any common interfaces.
        adjustToolbar(scrollState, view);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                toggleDrawer(drawerLayout);
                return true;
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class PageAdapter extends CacheFragmentStatePagerAdapter {

        private final List<String> CATEGORY = new ArrayList<>();
        private final List<String> COLORS = new ArrayList<>();
        private final List<String> COLORS_DARK = new ArrayList<>();
        private int mScrollY;

        public PageAdapter(FragmentManager fm, Context context) {
            super(fm);
            final String[] tempCat = context.getResources().getStringArray(R.array.category_name);
            if (tempCat == null || tempCat.length == 0)
                throw new IllegalStateException("You need at least 1 category");
            final String[] tempColors = context.getResources().getStringArray(R.array.category_colors);
            if (tempColors == null || tempColors.length == 0)
                throw new IllegalStateException("You need at least 1 color");
            final String[] tempColorsDark = context.getResources().getStringArray(R.array.category_colors_darker);
            if (tempColorsDark == null || tempColorsDark.length == 0)
                throw new IllegalStateException("You need at least 1 colorDark");
            if (tempColors.length != tempColorsDark.length)
                throw new IllegalStateException("You must have the same number of both colors");

            CATEGORY.addAll(Arrays.asList(tempCat));
            COLORS.addAll(Arrays.asList(tempColors));
            COLORS_DARK.addAll(Arrays.asList(tempColorsDark));

        }

        public void setScrollY(int scrollY) {
            mScrollY = scrollY;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        protected Fragment createItem(int i) {
            final Bundle bundle = new Bundle(3);
            bundle.putString(FeedsFragment.TITLE_KEY, CATEGORY.get(i));
            try {
                bundle.putInt(FeedsFragment.COLOR_KEY, getColor(i));
                bundle.putInt(FeedsFragment.COLOR_DARK_KEY, getColorDark(i));
            } catch (Exception e) {
                Log.d(TAG, "", e);
            }
            Fragment fragment = new FeedsFragment();
//            if (0 < mScrollY)
//                bundle.putInt(ARG_INITIAL_POSITION, 0);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return CATEGORY.size();
        }

        public String getTitle(int position) {
            return CATEGORY.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CATEGORY.get(position).toUpperCase();
        }

        public int getColor(int position) throws Exception {
            final int tempPosition = position % COLORS.size();
            return Color.parseColor(COLORS.get(tempPosition));
        }

        public int getColorDark(int position) throws Exception {
            final int tempPosition = position % COLORS_DARK.size();
            return Color.parseColor(COLORS_DARK.get(tempPosition));
        }
    }


}
