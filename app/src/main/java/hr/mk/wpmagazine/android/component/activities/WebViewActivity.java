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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.InjectView;
import hr.mk.wpmagazine.android.component.R;

/**
 * Created by Mur0 on 3/15/2015.
 */
public class WebViewActivity extends AbsBaseActivity {

    public static final String LINK_KEY = "LINK_KEY";
    public static final String COLOR_KEY = "COLOR_KEY";
    public static final String COLOR_KEY_DARK = "COLOR_KEY_DARK";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.progressWebView)
    ProgressBar progressBar;
    @InjectView(R.id.webView)
    WebView webView;
    private String link;

    private int color;
    private int colorDark;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.web_view_activity);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState == null) {
            link = getIntent().getExtras().getString(LINK_KEY);
            color = getIntent().getExtras().getInt(COLOR_KEY, 0);
            colorDark = getIntent().getExtras().getInt(COLOR_KEY_DARK);
        } else {
            colorDark = savedInstanceState.getInt(COLOR_KEY_DARK);
            color = savedInstanceState.getInt(COLOR_KEY);
            link = savedInstanceState.getString(LINK_KEY);
        }

        init();

        webView.setWebViewClient(new MyWebClient());
        webView.setWebChromeClient(new MyWebChromeClient(progressBar));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(link);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LINK_KEY, link);
        outState.putInt(COLOR_KEY, color);
        outState.putInt(COLOR_KEY_DARK, colorDark);
    }

    protected void init() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        if (color == 0 || colorDark == 0) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            setTint(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            toolbar.setBackgroundColor(color);
            setTint(colorDark);
            progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        toolbar.setTitle(getResources().getString(R.string.app_name));
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.web_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_open:
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
                break;
            case R.id.action_reload:
                if (webView != null)
                webView.reload();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private final static class MyWebChromeClient extends WebChromeClient {

        ProgressBar progressBar;

        private MyWebChromeClient(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
            if (newProgress >= 100) {
                progressBar.setVisibility(View.GONE);
            } else
                progressBar.setVisibility(View.VISIBLE);

        }
    }

    private final static class MyWebClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }
}
