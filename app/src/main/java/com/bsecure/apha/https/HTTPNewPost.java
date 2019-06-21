package com.bsecure.apha.https;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;


import com.bsecure.apha.R;
import com.bsecure.apha.callbacks.JsonHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by Admin on 2017-12-06.
 */

public class HTTPNewPost {
    private Context context = null;

    private JsonHandler callback = null;

    private int requestId = -1;

    private boolean progressFlag = true;

    private String contentType = "";

    private Object obj = null;

    private int cacheType = 0;

    private Dialog dialog = null;

    private GetConnection getConn = null;

    public HTTPNewPost(Context context, JsonHandler callback) {
        this.context = context;
        this.callback = callback;
    }

    public void disableProgress() {
        progressFlag = false;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void userRequest(String progressMsg, int requestId, final String url, final String postData, final int parserType) {

        this.requestId = requestId;
        Log.i("req_url",url);
        Log.i("post_data",postData);
        if (progressFlag)
             showProgress(progressMsg, context);

            if (!isNetworkAvailable()) {
                // showUserActionResult(-1, context.getString(R.string.nipcyns));
                disableProgress();
                progressFlag=false;
            }

        new Thread(new Runnable() {

            @Override
            public void run() {

                HttpURLConnection conn = null;
                DataOutputStream outputStream = null;
                InputStream inputStream = null;

                try {

                    String requestUrl = urlEncode(url);

                    getConn = new GetConnection(context);
                    getConn.setRequestMethod("POST");
                    conn = getConn.getHTTPConnection(requestUrl);

                    if (conn == null) {
                        //postUserAction(-1, context.getString(R.string.isr));
                        return;
                    }

                    inputStream = new ByteArrayInputStream(postData.getBytes());

                    outputStream = new DataOutputStream(conn.getOutputStream());

                    byte[] data = new byte[1024];
                    int bytesRead = 0;

                    while ((bytesRead = inputStream.read(data)) != -1) {
                        outputStream.write(data, 0, bytesRead);
                    }

                    int serverResponseCode = conn.getResponseCode();

                    String serverResponseMessage = conn.getResponseMessage();

                    if (serverResponseCode == 200) {

                        inputStream = conn.getInputStream();

                        byte[] bytebuf = new byte[0x1000];
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        for (; ; ) {
                            int len = inputStream.read(bytebuf);
                            if (len < 0)
                                break;
                            baos.write(bytebuf, 0, len);
                        }
                        bytebuf = baos.toByteArray();
                        obj = new String(bytebuf, "UTF-8");
                        Log.i("response",obj.toString());
                      //  Log.e("response::::", obj.toString() + "");

                        postUserAction(0, "");

                        return;
                    }

                    postUserAction(-1, serverResponseMessage);

                } catch (MalformedURLException me) {
                    dismissProgress();
                    // postUserAction(-1, context.getString(R.string.iurl));
                } catch (ConnectException e) {
                    dismissProgress();
                    //postUserAction(-1, context.getString(R.string.snr1));
                } catch (SocketException se) {
                    dismissProgress();
                    //postUserAction(-1, context.getString(R.string.snr2));
                } catch (SocketTimeoutException stex) {
                    dismissProgress();
                    // postUserAction(-1, context.getString(R.string.sct));
                } catch (Exception ex) {
                    dismissProgress();
                    //postUserAction(-1, context.getString(R.string.snr3));
                } finally {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                            inputStream = null;
                        } catch (IOException e) {
                            //TraceUtils.logException(e);
                        }

                    if (outputStream != null)
                        try {
                            outputStream.close();
                            outputStream = null;
                        } catch (IOException e) {
                            //TraceUtils.logException(e);
                        }

                    if (conn != null)
                        conn.disconnect();
                    conn = null;

                    if (getConn != null)
                        getConn.clearConn();
                    getConn = null;

                }
            }
        }).start();
    }

    private void postUserAction(final int status, final String response) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                showUserActionResult(status, response);

            }
        });
    }

    private void showUserActionResult(int status, String response) {

        dismissProgress();

        switch (status) {
            case 0:
                callback.onResponse(obj, requestId);
                break;

            case -1:
                callback.onFailure(response, requestId);
                break;

            default:
                break;
        }

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo net = manager.getActiveNetworkInfo();
        return net != null && net.isConnected();

    }

    private String urlEncode(String sUrl) {
        int i = 0;
        String urlOK = "";
        while (i < sUrl.length()) {
            if (sUrl.charAt(i) == ' ') {
                urlOK = urlOK + "%20";
            } else {
                urlOK = urlOK + sUrl.charAt(i);
            }
            i++;
        }
        return (urlOK);
    }

    private void showProgress(String title, Context context) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        View view = View.inflate(context, R.layout.processing, null);

        dialog.setContentView(view);
        dialog.show();

    }

    private void dismissProgress() {
        try {

            if (dialog != null)
                dialog.dismiss();
            dialog = null;

        } catch (Exception e) {
            //TraceUtils.logException(e);
        }
    }

}
