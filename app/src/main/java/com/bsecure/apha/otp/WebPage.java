package com.bsecure.apha.otp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.bsecure.apha.R;

public class WebPage extends AppCompatActivity {
    private WebView wv_content = null;
    private WebSettings webSettings = null;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);

        Intent data=getIntent();
        String url=data.getStringExtra("url");
        if (url.startsWith("www")){
            url="http://"+url;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        wv_content = (WebView) findViewById(R.id.wwvv);

        wv_content.getSettings().setAllowFileAccess(true);
        wv_content.getSettings().setSupportZoom(true);
        wv_content.setVerticalScrollBarEnabled(true);
        wv_content.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv_content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        wv_content.getSettings().setLoadWithOverviewMode(true);
        wv_content.getSettings().setUseWideViewPort(true);
        wv_content.getSettings().setJavaScriptEnabled(true);
        wv_content.getSettings().setPluginState(WebSettings.PluginState.ON);

        wv_content.getSettings().setSaveFormData(false);
        wv_content.getSettings().setSavePassword(false);

        wv_content.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv_content.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        wv_content.setWebViewClient(new MyWebViewClient());
        wv_content.setWebChromeClient(new MyWebChromeClient());

        wv_content.getSettings().setJavaScriptEnabled(true);
        wv_content.getSettings().setLoadWithOverviewMode(true);
        wv_content.getSettings().setUseWideViewPort(true);

        webSettings = wv_content.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        String abt = url;
        wv_content.loadUrl(abt);

    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            // Log.e("-=-=-=-=-=-", newProgress + "");

            if (newProgress == 5)
                findViewById(R.id.pr_bar).setVisibility(View.VISIBLE);

            if (newProgress >= 95) {
                findViewById(R.id.pr_bar).setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            findViewById(R.id.pr_bar).setVisibility(View.GONE);
        }

    }

    private void close() {
        if (wv_content != null) {
            wv_content.stopLoading();
            wv_content.setBackgroundDrawable(null);
            wv_content.clearFormData();
            wv_content.clearHistory();
            wv_content.clearMatches();
            wv_content.clearCache(true);
            wv_content.clearSslPreferences();
            wv_content = null;
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                WebPage.this.finish();

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}