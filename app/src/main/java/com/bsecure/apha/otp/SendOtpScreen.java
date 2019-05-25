package com.bsecure.apha.otp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.R;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.database.DB_Tables;
import com.bsecure.apha.firebasepaths.SharedPrefManager;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.utils.SharedValues;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_MMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECEIVE_WAP_PUSH;
import static android.Manifest.permission.SEND_SMS;

public class SendOtpScreen extends AppCompatActivity implements JsonHandler {

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId, reg_mobile_no;
    private DB_Tables db_tables;
    SweetAlertDialog pDialog;
    String number_field;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_otp_scone);
        mAuth = FirebaseAuth.getInstance();
        db_tables = new DB_Tables(this);
        db_tables.openDB();
//        if (CheckingPermissionIsEnabledOrNot()) {
//        } else {
//            RequestMultiplePermission();
//        }
        findViewById(R.id.send_otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSendOtp();
            }
        });
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String opt = ((EditText) findViewById(R.id.f_otp)).getText().toString();
                if (opt.length() == 0) {
                    ((EditText) findViewById(R.id.f_otp)).setError("Enter OTP");
                    return;
                }
                if (opt.length() < 4) {
                    ((EditText) findViewById(R.id.f_otp)).setError("Wrong OTP");
                    return;
                }
                getvery(opt);
            }
        });
    }

    private void getvery(String opt) {
        try {

            reg_mobile_no = ((EditText) findViewById(R.id.f_number)).getText().toString();
            if (reg_mobile_no.length() == 0) {
                ((EditText) findViewById(R.id.f_number)).setError("Enter Mobile Number");
                return;
            }
            JSONObject object = new JSONObject();
            object.put("phone_no", reg_mobile_no);
            object.put("otp", opt);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 12, Paths.otp_verify, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSendOtp() {

        try {
            String mobile_no = ((EditText) findViewById(R.id.f_number)).getText().toString();
            if (mobile_no.length() == 0) {
                ((EditText) findViewById(R.id.f_number)).setError("Enter Mobile Number");
                return;
            }
            if (mobile_no.length() < 10) {
                ((EditText) findViewById(R.id.f_number)).setError("Enter Valid Mobile Number");
                return;
            }

            findViewById(R.id.register).setVisibility(View.VISIBLE);
            findViewById(R.id.f_number).setVisibility(View.INVISIBLE);
            findViewById(R.id.send_otp).setVisibility(View.GONE);
            findViewById(R.id.f_otp).setVisibility(View.VISIBLE);
            findViewById(R.id.register).setEnabled(true);

            reg_mobile_no = ((EditText) findViewById(R.id.f_number)).getText().toString();
            if (reg_mobile_no.length() == 0) {
                ((EditText) findViewById(R.id.f_number)).setError("Enter Mobile Number");
                return;
            }
            JSONObject object = new JSONObject();
            object.put("reg_mobile_no", reg_mobile_no);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 11, Paths.otp_sc, object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                ((EditText) findViewById(R.id.f_otp)).setText(message);
                findViewById(R.id.register).setVisibility(View.VISIBLE);
                findViewById(R.id.f_number).setVisibility(View.INVISIBLE);
                findViewById(R.id.send_otp).setVisibility(View.GONE);
                findViewById(R.id.f_otp).setVisibility(View.VISIBLE);
                findViewById(R.id.register).setEnabled(true);

            }
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public boolean CheckingPermissionIsEnabledOrNot() {

        int one = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        int two = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int four = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_WAP_PUSH);
        int six = ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS);
        int five = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_MMS);

        return one == PackageManager.PERMISSION_GRANTED &&
                two == PackageManager.PERMISSION_GRANTED &&
                four == PackageManager.PERMISSION_GRANTED &&six == PackageManager.PERMISSION_GRANTED &&
                five == PackageManager.PERMISSION_GRANTED;
    }

    //Permission function starts from here
    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, new String[]
                {
                        RECEIVE_SMS, SEND_SMS,RECEIVE_WAP_PUSH,READ_SMS, RECEIVE_MMS

                }, 56);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 56:

                if (grantResults.length > 0) {

                    boolean one = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean two = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean four = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean five = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean six = grantResults[4] == PackageManager.PERMISSION_GRANTED;

                    if (one && six && two&& four&& six&& five) {
                        // Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();

                    } else {
                        //Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    private void getRegisterAPI() {
        try {
            // pDialog.dismiss();
            reg_mobile_no = ((EditText) findViewById(R.id.f_number)).getText().toString();
            if (reg_mobile_no.length() == 0) {
                ((EditText) findViewById(R.id.f_number)).setError("Enter Mobile Number");
                return;
            }
            JSONObject object = new JSONObject();
            object.put("reg_mobile_no", reg_mobile_no);
            object.put("regidand", SharedPrefManager.getInstance(this).getDeviceToken());
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.member_verify, object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {
        try {

            switch (requestType) {
                case 1:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        String status = object.optString("status");
                        if (status.equalsIgnoreCase("0")) {
                            getSweetAlert(object.optString("statusdescription"));
                            return;
                        }
                        String member_name = object.optString("member_name");
                        String member_id = object.optString("member_id");
                        String approval_status = object.optString("approval_status");
                        String member_number = object.optString("member_number");
                        String paid_status = object.optString("paid_status");
                        String subscription_status = object.optString("subscription_status");
                        String business_name = object.optString("business_name");
                        String state_id = object.optString("state_id");
                        String state = object.optString("state");
                        String district_id = object.optString("district_id");
                        String district = object.optString("district");
                        String added_date = object.optString("added_date");
                        String expiry_date = object.optString("expiry_date");
                        String user_pic = object.optString("profile_image");
                        String designation_name = object.optString("designation_name");

                        SharedValues.saveValue(this, "member_id", member_id);
                        SharedValues.saveValue(this, "reg_mobile_no", reg_mobile_no);
                        SharedValues.saveValue(this, "sender_name", member_name);
                        SharedValues.saveValue(this, "district_id", district_id);
                        SharedValues.saveValue(this, "member_number", member_number);
                        SharedValues.saveValue(this, "business_name", business_name);
                        SharedValues.saveValue(this, "paid_status", paid_status);
                        SharedValues.saveValue(this, "subscription_status", subscription_status);
                        SharedValues.saveValue(this, "approval_status", approval_status);
                        SharedValues.saveValue(this, "user_pic", user_pic);
                        SharedValues.saveValue(this, "state", state);
                        SharedValues.saveValue(this, "district", district);
                        SharedValues.saveValue(this, "designation_name", designation_name);
                        db_tables.addToMembers(member_id, member_name, approval_status, member_number, paid_status, subscription_status, business_name, reg_mobile_no, state_id, state, district_id, district, added_date, expiry_date, user_pic);
                        Intent sc = new Intent(getApplicationContext(), AccociateMain.class);
                        startActivity(sc);
                        finish();
                    } else {
                        getError(object.optString("statusdescription"));
                    }
                    break;
                case 11:
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        findViewById(R.id.register).setVisibility(View.VISIBLE);
                        findViewById(R.id.f_number).setVisibility(View.INVISIBLE);
                        findViewById(R.id.send_otp).setVisibility(View.GONE);
                        findViewById(R.id.f_otp).setVisibility(View.VISIBLE);
                        findViewById(R.id.register).setEnabled(true);
                        Toast.makeText(this, object1.optString("statusdescription"), Toast.LENGTH_LONG).show();
                    }
                    break;
                case 12:
                    JSONObject object11 = new JSONObject(results.toString());
                    if (object11.optString("statuscode").equalsIgnoreCase("200")) {
                        Toast.makeText(this, object11.optString("statusdescription"), Toast.LENGTH_LONG).show();
                        getRegisterAPI();

                    } else {
                        findViewById(R.id.register).setVisibility(View.GONE);
                        findViewById(R.id.f_number).setVisibility(View.VISIBLE);
                        findViewById(R.id.send_otp).setVisibility(View.VISIBLE);
                        findViewById(R.id.f_otp).setVisibility(View.GONE);
                        findViewById(R.id.register).setEnabled(true);
                        Toast.makeText(this, object11.optString("statusdescription"), Toast.LENGTH_LONG).show();
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getError(String text) {

        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Alert!")
                .setContentText(text)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent sc = new Intent(getApplicationContext(), SendOtpScreenTwo.class);
                        startActivity(sc);
                        SendOtpScreen.this.finish();
                    }
                })
                .show();

    }

    private void getSweetAlert(String text) {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Alert!")
                .setContentText(text)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        SendOtpScreen.this.finish();
                    }
                })
                .show();
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }
}

