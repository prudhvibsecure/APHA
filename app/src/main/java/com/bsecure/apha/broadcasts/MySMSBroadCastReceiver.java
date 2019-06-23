package com.bsecure.apha.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by Admin on 2018-10-18.
 */

public class MySMSBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get Bundle object contained in the SMS intent passed in

        try {


            Bundle bundle = intent.getExtras();
            SmsMessage[] smsm = null;
            String sms_str = "";

            if (bundle != null) {
                // Get the SMS message
                Object[] pdus = (Object[]) bundle.get("pdus");
                smsm = new SmsMessage[pdus.length];
                for (int i = 0; i < smsm.length; i++) {
                    smsm[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    sms_str += smsm[i].getMessageBody().toString();
                    sms_str = sms_str.replaceAll("[^0-9]", "");
                    if (sms_str != null || sms_str.length() != 0) {

                        String Sender = smsm[i].getOriginatingAddress();
                        //Check here sender is yours
                        Intent smsIntent = new Intent("otp");
                        smsIntent.putExtra("message", sms_str);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
                    } else {

                        Toast.makeText(context, "OTP failed", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
