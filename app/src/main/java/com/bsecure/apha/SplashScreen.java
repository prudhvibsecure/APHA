package com.bsecure.apha;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bsecure.apha.otp.SendOtpScreen;
import com.bsecure.apha.utils.SharedValues;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_v);

        final String member_id = SharedValues.getValue(this, "member_id");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (member_id == null || member_id.length() == 0) {
                    Intent nxt = new Intent(getApplicationContext(), SendOtpScreen.class);
                    startActivity(nxt);
                    finish();
                } else {
                    Intent nxt = new Intent(getApplicationContext(), AccociateMain.class);
                    startActivity(nxt);
                    finish();
                }

            }
        }, 2000);
    }
}
