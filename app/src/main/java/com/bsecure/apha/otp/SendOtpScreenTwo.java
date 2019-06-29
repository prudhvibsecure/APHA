package com.bsecure.apha.otp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.R;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.database.DB_Tables;
import com.bsecure.apha.firebasepaths.SharedPrefManager;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.models.DistrictModel;
import com.bsecure.apha.models.StatesModel;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;

public class SendOtpScreenTwo extends AppCompatActivity implements JsonHandler {

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId, ht_access;
    ArrayList<StatesModel> statesModelArrayList;
    ArrayList<DistrictModel> districtModelArrayList;
    JSONArray jsonarray;
    private Spinner state_spinner, dist_spinner;
    private String reg_mobile_no;
    private DB_Tables db_tables;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_otp_sc_two);
        db_tables = new DB_Tables(this);
        db_tables.openDB();
        state_spinner = findViewById(R.id.f_state);
        dist_spinner = findViewById(R.id.f_dist);

        Intent in = getIntent();
        if(in!= null)
        {
            reg_mobile_no = in.getStringExtra("mobile");
        }
        EditText fnum = findViewById(R.id.f_number);
        fnum.setText(reg_mobile_no);
        mAuth = FirebaseAuth.getInstance();

        if (CheckingPermissionIsEnabledOrNot()) {
        } else {
            RequestMultiplePermission();
        }
        getStates();
        statesModelArrayList = new ArrayList<>();
        districtModelArrayList = new ArrayList<>();
        findViewById(R.id.send_otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getSendOtp();
                getRegisterAPI();
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
                if (opt.length() < 6) {
                    ((EditText) findViewById(R.id.f_otp)).setError("Wrong OTP");
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, opt);
                signInWithPhoneAuthCredential(credential);
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                mVerificationInProgress = false;
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // MyDynamicToast.errorMessage(Registration.this, "Verification Failed");
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    //   MyDynamicToast.warningMessage(Registration.this, "InValid Phone Number");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void getStates() {
        HTTPNewPost task = new HTTPNewPost(this, this);
        task.disableProgress();
        task.userRequest("Processing...", 2, Paths.get_states, "", 1);
    }

    private void getSendOtp() {

        String member = ((EditText) findViewById(R.id.f_member)).getText().toString();
        if (member.length() == 0) {
            ((EditText) findViewById(R.id.f_member)).setError("Enter Member Name");
            return;
        }
        String member_id = ((EditText) findViewById(R.id.f_member_id)).getText().toString();
        if (member_id.length() == 0) {
            ((EditText) findViewById(R.id.f_member_id)).setError("Enter Member Number");
            return;
        }
        String business = ((EditText) findViewById(R.id.f_business)).getText().toString();
        if (business.length() == 0) {
            ((EditText) findViewById(R.id.f_business)).setError("Enter Business Name");
            return;
        }
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
        findViewById(R.id.f_number).setVisibility(View.GONE);
        findViewById(R.id.send_otp).setVisibility(View.GONE);
        findViewById(R.id.f_member).setVisibility(View.INVISIBLE);
        findViewById(R.id.f_member_id).setVisibility(View.GONE);
        findViewById(R.id.f_business).setVisibility(View.GONE);
        findViewById(R.id.f_state).setVisibility(View.GONE);
        findViewById(R.id.f_dist).setVisibility(View.GONE);
        findViewById(R.id.f_otp).setVisibility(View.VISIBLE);
        findViewById(R.id.register).setEnabled(true);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile_no,
                60,
                TimeUnit.SECONDS,
                SendOtpScreenTwo.this,
                mCallbacks);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        try {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                getRegisterAPI();
                            } else {
                                try {
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        // MyDynamicToast.errorMessage(Registration.this, "Invalid Verification");
                                        Toast.makeText(SendOtpScreenTwo.this, "Invalid Verification", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(SendOtpScreenTwo.this, "Invalid Verification", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
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

        int five = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        int six = ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS);
        int seven = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);

        return five == PackageManager.PERMISSION_GRANTED &&
                six == PackageManager.PERMISSION_GRANTED &&
                seven == PackageManager.PERMISSION_GRANTED;
    }

    //Permission function starts from here
    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, new String[]
                {
                        RECEIVE_SMS, READ_SMS, READ_PHONE_STATE

                }, 56);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 56:

                if (grantResults.length > 0) {

                    boolean five = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean six = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean seven = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (five && six && seven) {
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

            String member = ((EditText) findViewById(R.id.f_member)).getText().toString().trim();
            if (member.length() == 0) {
                ((EditText) findViewById(R.id.f_member)).setError("Please Enter Member Name");
                return;
            }
            String member_id = ((EditText) findViewById(R.id.f_member_id)).getText().toString().trim();
            if (member_id.length() == 0) {
                ((EditText) findViewById(R.id.f_member_id)).setError("Please Enter Member ID");
                return;
            }
            reg_mobile_no = ((EditText) findViewById(R.id.f_number)).getText().toString().trim();
            if (reg_mobile_no.length() == 0) {
                ((EditText) findViewById(R.id.f_number)).setError("Please Enter Mobile Number");
                return;
            }
            if (reg_mobile_no.length() < 10) {
                ((EditText) findViewById(R.id.f_number)).setError("Please Enter Valid Mobile Number");
                return;
            }
            String business = ((EditText) findViewById(R.id.f_business)).getText().toString().trim();
            if (business.length() == 0) {
                ((EditText) findViewById(R.id.f_business)).setError("Please Enter Business Name");
                return;
            }
            StatesModel selectedState = (StatesModel) state_spinner.getSelectedItem();
            if (selectedState == null) {
                Toast.makeText(this, "Please Select State", Toast.LENGTH_SHORT).show();
                return;
            }
            String state_name = selectedState.getState_name();
            if (state_name.equalsIgnoreCase("Select State")) {
                Toast.makeText(this, "Please Select State", Toast.LENGTH_SHORT).show();
                return;
            }
            DistrictModel selectedDist = (DistrictModel) dist_spinner.getSelectedItem();
            if (selectedDist == null) {
                Toast.makeText(this, "Please Select District", Toast.LENGTH_SHORT).show();
                return;
            }
            String district_name = selectedDist.getDist_name();
            if (district_name.equalsIgnoreCase("Select District")) {
                Toast.makeText(this, "Please Select District", Toast.LENGTH_SHORT).show();
                return;
            }
            String state_id = selectedState.getState_id();
            String dist_id = selectedDist.getDist_id();
            JSONObject object = new JSONObject();
            object.put("member_name", member);
            object.put("member_id", member_id);
            object.put("reg_mobile_no", reg_mobile_no);
            object.put("business_name", business);
            object.put("state", state_name);
            object.put("state_id", state_id);
            object.put("district", district_name);
            object.put("district_id", dist_id);
            object.put("regidand", SharedPrefManager.getInstance(this).getDeviceToken());
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.member_register, object.toString(), 1);

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
                            getSweetAlertError(object.optString("statusdescription"));
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
                        String website = object.optString("website");
                        String email = object.optString("email");


                        SharedValues.saveValue(this, "member_id", member_id);
                        SharedValues.saveValue(this, "reg_mobile_no", reg_mobile_no);
                        SharedValues.saveValue(this, "sender_name", member_name);
                        SharedValues.saveValue(this, "district_id", district_id);
                        SharedValues.saveValue(this, "member_number", member_number);
                        SharedValues.saveValue(this, "business_name", business_name);
                        SharedValues.saveValue(this, "user_pic", user_pic);
                        SharedValues.saveValue(this, "paid_status", paid_status);
                        SharedValues.saveValue(this, "subscription_status", subscription_status);
                        SharedValues.saveValue(this, "approval_status", approval_status);
                        SharedValues.saveValue(this, "user_pic", user_pic);
                        SharedValues.saveValue(this, "state", state);
                        SharedValues.saveValue(this, "district", district);
                        SharedValues.saveValue(this, "designation_name", designation_name);
                        SharedValues.saveValue(this, "website", website);
                        SharedValues.saveValue(this, "email", email);

                        db_tables.addToMembers(member_id, member_name, approval_status, member_number, paid_status, subscription_status, business_name, reg_mobile_no, state_id, state, district_id, district, added_date, expiry_date, user_pic);
                        getSweetAlert(object.optString("statusdescription"));
                    } else {
                        getSweetAlertError(object.optString("statusdescription"));
                    }
//                    Toast.makeText(this, "statusdescription", Toast.LENGTH_SHORT).show();
                    break;
                case 2:

                    JSONObject obj = new JSONObject(results.toString());
                    if (obj.optString("statuscode").equalsIgnoreCase("200")) {
                        jsonarray = obj.getJSONArray("state_details");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            StatesModel cityy = new StatesModel();

                            cityy.setState_id(jsonobject.optString("state_id"));
                            cityy.setState_name(jsonobject.optString("state"));
                            statesModelArrayList.add(cityy);

                        }
                        state_spinner.setAdapter(new ArrayAdapter<StatesModel>(SendOtpScreenTwo.this,
                                R.layout.spinner_row_nothing_selected, statesModelArrayList));
                        state_spinner.setPrompt("Select State");

                        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                                findViewById(R.id.f_dist).setVisibility(View.VISIBLE);
                                StatesModel selectedState = (StatesModel) state_spinner.getSelectedItem();
                                try {

                                    JSONObject object = new JSONObject();
                                    object.put("state_id", selectedState.getState_id());
                                    HTTPNewPost task = new HTTPNewPost(SendOtpScreenTwo.this, SendOtpScreenTwo.this);
                                    task.disableProgress();
                                    task.userRequest("Processing...", 3, Paths.get_district, object.toString(), 1);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                                // TODO Auto-generated method stub
                            }
                        });
                    }
                    break;

                case 3:
                    JSONObject obje = new JSONObject(results.toString());
                    if (obje.optString("statuscode").equalsIgnoreCase("200")) {
                        jsonarray = obje.getJSONArray("district_details");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            DistrictModel districtModel = new DistrictModel();

                            districtModel.setDist_id(jsonobject.optString("district_id"));
                            districtModel.setDist_name(jsonobject.optString("district"));

                            districtModelArrayList.add(districtModel);

                        }
                        dist_spinner.setAdapter(new ArrayAdapter<DistrictModel>(SendOtpScreenTwo.this,
                                R.layout.spinner_row_nothing_selected, districtModelArrayList));
                        dist_spinner.setPrompt("Select District");
                        dist_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                                // TODO Auto-generated method stub
                            }
                        });
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    private void getSweetAlert(String text) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Alert!")
                .setContentText(text)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        reportnext();
                    }
                })
                .show();
    }

    private void reportnext() {
        Intent sc = new Intent(getApplicationContext(), AccociateMain.class);
        startActivity(sc);
        finish();
    }

    private void getSweetAlertError(String text) {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Alert!")
                .setContentText(text)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();
    }

}

