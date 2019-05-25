package com.bsecure.apha.otp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bsecure.apha.R;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.utils.SharedValues;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MPayment extends AppCompatActivity implements OnClickListener, JsonHandler {

    private WebView webview = null;
    private WebSettings webSettings = null;
    private String callbackURL = "";

    private Toolbar toolbar = null;

    private ContainerScriptInterface jsInterface = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_payment);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String title = getString(R.string.payment);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(title);
        getSupportActionBar().setTitle(title);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                close();
            }
        });

        try {

            // String locale = AppPreferences.getInstance(this).getFromStore("country");
            String link = Paths.pay_path + SharedValues.getValue(this, "reg_mobile_no");
//            link = link.replace("(EMAIL)", AppPreferences.getInstance(this).getFromStore("email"));
//            link = link.replace("(COUNTRY)", locale);
//            HTTPTask task = new HTTPTask(this, this);
//            task.disableProgress();
//            task.userRequest("", 1, link);
            init(link);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void init(String url) {

        jsInterface = new ContainerScriptInterface();

        webview = (WebView) findViewById(R.id.wv_paymentview);

        webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.addJavascriptInterface(jsInterface, "jsInterface");
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        webview.setWebViewClient(new MyWebViewClient());
        webview.setWebChromeClient(new MyWebChromeClient());

        webview.requestFocus(View.FOCUS_DOWN);
        webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
        Map<String, String> map = new HashMap<String, String>();
        webview.loadUrl(url, map);
    }

    /**
     * (non-Javadoc)
     *
     * @see WebView#onKeyDown(int, KeyEvent)
     */
    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // return false;
    // }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 4) {

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResponse(Object results, int requestType) {

    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    /**
     * MyWebChromeClient - class which extend WebChromeClient for handle page
     * progress.
     *
     * @author shadab
     */
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if (newProgress == 5)
                findViewById(R.id.pb_payment).setVisibility(View.VISIBLE);

            if (newProgress >= 95) {
                findViewById(R.id.pb_payment).setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
    }

    /**
     * MyWebViewClient - class which extends WebViewClient to handle internal
     * URL.
     *
     * @author shadab
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Map<String, String> map = new HashMap<String, String>();
            view.loadUrl(url, map);
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            findViewById(R.id.pb_payment).setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);
        }
    }

    /**
     * close - When payment is done this method is called to release the
     * resource.
     */
    private void close() {
        if (webview != null) {
            webview.stopLoading();
            webview.setBackgroundDrawable(null);
            webview.clearFormData();
            webview.clearHistory();
            webview.clearMatches();
            webview.clearCache(true);
            webview.clearSslPreferences();
            webview = null;
        }

        finish();
    }

    public class ContainerScriptInterface {

        public ContainerScriptInterface() {
        }

        @JavascriptInterface
        public void exec(String service, String action, String arg) throws JSONException {

            try {
                showToast(service + "-----" + action + " -------- " + arg);
                // Log.e("--------------", "service : " + service + " :::
                // action:" + action + " ::::: arg:" + arg);

                showOkMessage(getString(R.string.payment), arg + "", action);

            } catch (Throwable e) {

            }
        }

    }

    public void showToast(String value) {
        // Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//
//		case R.id.tv_upgradenow:
//
//			Intent intent = new Intent();
//			intent.putExtra("action", view.getTag() + "");
//			setResult(RESULT_OK, intent);
//			MPayment.this.finish();
//
//			break;

            default:
                break;
        }
    }

    public void showOkMessage(String title, String message, String action) {

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        bundle.putString("action", action);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        //MessageDialog.newInstance(bundle).show(MPayment.this.getSupportFragmentManager(), "dialog");

    }

}
