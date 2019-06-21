package com.bsecure.apha.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.R;
import com.bsecure.apha.adapters.APHAListAdapter;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.database.DB_Tables;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.models.MemberModel;
import com.bsecure.apha.otp.MPayment;
import com.bsecure.apha.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Admin on 2018-06-11.
 */

public class APHSFragment extends ParentFragment implements View.OnClickListener, JsonHandler, APHAListAdapter.ContactAdapterListener {

    private View layout;
    private AccociateMain activity;
    private DB_Tables db_tables;
    String member_number, sender_member_id, receiver_member_ids, district_id, sender_name;
    String action = "0", receiver_member_number;
    private APHAListAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<MemberModel> memberModelArrayList;
    NetworkChangeReceiver mNetworkReceiver;
    HTTPNewPost task;

    public APHSFragment() {

    }

    public static APHSFragment newInstance() {
        return new APHSFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.frg_three, container, false);
        db_tables = new DB_Tables(getActivity());
        db_tables.openDB();
        recyclerView = layout.findViewById(R.id.content_v);
        memberModelArrayList = new ArrayList<>();

        // getStudents();
        try {
            mNetworkReceiver = new NetworkChangeReceiver();
            getActivity().registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return layout;
    }

    private void getStudents() {

        try {
            JSONObject object = new JSONObject();
            //object.put("member_number", SharedValues.getValue(getActivity(), "member_number"));
            task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Processing...", 1, Paths.get_apha_list, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AccociateMain) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

    }

    void getredAlert(String text, final String condition) {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Alert!")
                .setContentText(text)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        if (condition.equalsIgnoreCase("1")) {
                            Intent pay = new Intent(getActivity(), MPayment.class);
                            startActivity(pay);
                        }
                    }
                })
                .show();
    }

    @Override
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {
                case 1:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray jsonarray2 = object.getJSONArray("member_details");
                        if (jsonarray2.length() > 0) {
                            SharedValues.saveValue(getActivity(), "member_details", jsonarray2.toString());
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                MemberModel memberModel = new MemberModel();
                                memberModel.setId(jsonobject.optString("id"));
                                memberModel.setMember_number(jsonobject.optString("member_number"));
                                memberModel.setName(jsonobject.optString("name"));
                                memberModel.setLast_msg(jsonobject.optString("last_message"));
                                memberModel.setTime(jsonobject.optString("time"));
                                memberModelArrayList.add(memberModel);
                            }

                            adapter = new APHAListAdapter(memberModelArrayList, getActivity(), this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                        }

                    } else {
                        getSweetAlert("No data found");
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSweetAlert(String text) {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Alert!")
                .setContentText(text)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        activity.onKeyDown(4, null);
                        sDialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    @Override
    public void onMessageRowClicked(List<MemberModel> matchesList, int position) {
        if (SharedValues.getValue(getActivity(), "paid_status").equalsIgnoreCase("1") && SharedValues.getValue(getActivity(), "subscription_status").equalsIgnoreCase("1") && SharedValues.getValue(getActivity(), "approval_status").equalsIgnoreCase("1")) {
            String member_num = matchesList.get(position).getMember_number();
            if (member_num.equalsIgnoreCase("0")) {
                activity.getRolesList("2", matchesList.get(position).getId());
            } else {
                activity.getRolesList("1", matchesList.get(position).getId());
            }
        } else if (SharedValues.getValue(getActivity(), "approval_status").equalsIgnoreCase("0")) {
            getredAlert("Your Approval Is Pending \nPlease Contact Admin", "0");
        } else if (SharedValues.getValue(getActivity(), "paid_status").equalsIgnoreCase("0")) {
            getredAlert("Your Membership Payment Is Pending \nPlease Contact Admin", "0");
        } else if (SharedValues.getValue(getActivity(), "subscription_status").equalsIgnoreCase("0")) {
            getredAlert("Your App Subscription Is Pending \nPlease Contact Admin", "1");
        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (isOnline(context)) {
                    //dialog(true);
                    String member_details = SharedValues.getValue(getActivity(), "member_details");
                    if (member_details == null) {
                        getStudents();
                    } else {
                        getCacheResponse(member_details);
                    }


                    Log.e("prudhvi", "Online Connect Intenet ");
                } else {
                    // callMessageRaed(false);
                    // dialog(false);
                    Toast.makeText(context, "Network connection failed", Toast.LENGTH_SHORT).show();
                    Log.e("prudhvi", "Conectivity Failure !!! ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean isOnline(Context context) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //should check null because in airplane mode it will be null
                return (netInfo != null && netInfo.isConnected());
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }
        }

    }

    private void getCacheResponse(String member_details) {
        try {
            JSONArray myCacheArray = new JSONArray(member_details);
            if (myCacheArray.length() > 0) {
                for (int i = 0; i < myCacheArray.length(); i++) {
                    JSONObject jsonobject = myCacheArray.getJSONObject(i);
                    MemberModel memberModel = new MemberModel();
                    memberModel.setId(jsonobject.optString("id"));
                    memberModel.setMember_number(jsonobject.optString("member_number"));
                    memberModel.setName(jsonobject.optString("name"));
                    memberModel.setLast_msg(jsonobject.optString("last_message"));
                    memberModel.setTime(jsonobject.optString("time"));
                    memberModelArrayList.add(memberModel);
                }

                adapter = new APHAListAdapter(memberModelArrayList, getActivity(), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
