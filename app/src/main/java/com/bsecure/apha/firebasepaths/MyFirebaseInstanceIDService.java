package com.bsecure.apha.firebasepaths;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by Admin on 2018-09-27.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public static final String TOKEN_VALUE = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TOKEN_VALUE, "Refreshed token: " + refreshedToken);
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        //saving the token on shared preferences
        //AppPreferences.getInstance(getApplicationContext()).addToStore("regID_majic", token);
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
    }
}
