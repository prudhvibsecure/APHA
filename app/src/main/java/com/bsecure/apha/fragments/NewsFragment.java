package com.bsecure.apha.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.R;
import com.bsecure.apha.adapters.MemberListAdapter;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.chatview.ChatSingle;
import com.bsecure.apha.chatview.ViewChatSingle;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.database.DB_Tables;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.models.MemberModel;
import com.bsecure.apha.otp.MPayment;
import com.bsecure.apha.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Admin on 2018-06-11.
 */

public class NewsFragment extends ParentFragment implements JsonHandler, MemberListAdapter.ContactAdapterListener {

    private View layout;
    private DB_Tables db_tables;
    private AccociateMain activity;
    private MemberListAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<MemberModel> memberModelArrayList;
    private IntentFilter myIntentFilter;
    NetworkChangeReceiver mNetworkReceiver;

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db_tables = new DB_Tables(getActivity());
        db_tables.openDB();
        layout = inflater.inflate(R.layout.frg_three, container, false);
        myIntentFilter = new IntentFilter("com.acc.app.BROADCAST_NOTIFICATION");
        recyclerView = layout.findViewById(R.id.content_v);
        memberModelArrayList = new ArrayList<>();
        try {
            mNetworkReceiver = new NetworkChangeReceiver();
            getActivity().registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkPermission();

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mBroadcastReceiver);
            getActivity().unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String myaction = intent.getAction();
            try {
                if (myaction.equals("com.acc.app.BROADCAST_NOTIFICATION")) {
                    //action = intent.getExtras().getString("NotificationData");
                    // receiver_member_number = intent.getExtras().getString("receiver_member_number");
                    //  getViewSupport(action);
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    getmemberList();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void getStudents() {

        try {
            JSONObject object = new JSONObject();
            object.put("member_number", SharedValues.getValue(getActivity(), "member_number"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.disableProgress();
            task.userRequest("Processing...", 1, Paths.get_news_list, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AccociateMain) context;
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
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                db_tables.addMemberList(jsonobject.optString("id"), jsonobject.optString("name"), jsonobject.optString("member_number"));
                            }
                        }
                        getmemberList();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getmemberList() {

        try {
            Object msg_list = db_tables.getmember_number(SharedValues.getValue(getActivity(), "district_id"));
            JSONObject obj = new JSONObject(msg_list.toString());
            JSONArray jsonarray2 = obj.getJSONArray("member_details");

            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    MemberModel memberModel = new MemberModel();
                    memberModel.setId(jsonobject.optString("id"));
                    memberModel.setMember_number(jsonobject.optString("member_number"));
                    memberModel.setName(jsonobject.optString("name"));
                    memberModel.setLast_msg(jsonobject.optString("last_message"));
                    memberModel.setTime(jsonobject.optString("time"));
                    memberModel.setRead(jsonobject.optString("flag"));
                    memberModelArrayList.add(memberModel);

                }
                adapter = new MemberListAdapter(memberModelArrayList, getActivity(), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }
    @Override
    public String getFragmentName() {
        return "APHA";
    }
    @Override
    public void onMessageRowClicked(List<MemberModel> matchesList, int position) {

        if (SharedValues.getValue(getActivity(), "paid_status").equalsIgnoreCase("1") && SharedValues.getValue(getActivity(), "subscription_status").equalsIgnoreCase("1") && SharedValues.getValue(getActivity(), "approval_status").equalsIgnoreCase("1")) {
            if (SharedValues.getValue(getActivity(), "member_number").equalsIgnoreCase("4")) {
                Intent chat_sc = new Intent(getActivity(), ViewChatSingle.class);
                chat_sc.putExtra("member_number", SharedValues.getValue(getActivity(), "member_number"));
                chat_sc.putExtra("presedent_id", matchesList.get(position).getMember_number());
                chat_sc.putExtra("id", matchesList.get(position).getId());
                chat_sc.putExtra("sender_name", SharedValues.getValue(getActivity(), "sender_name"));
                chat_sc.putExtra("sender_member_id", SharedValues.getValue(getActivity(), "member_id"));
                chat_sc.putExtra("receiver_member_number", matchesList.get(position).getMember_number());
                chat_sc.putExtra("name", matchesList.get(position).getName());
                chat_sc.putExtra("read", matchesList.get(position).getRead());
                startActivity(chat_sc);
            } else {
                Intent chat_sc = new Intent(getActivity(), ChatSingle.class);
                chat_sc.putExtra("member_number", SharedValues.getValue(getActivity(), "member_number"));
                chat_sc.putExtra("presedent_id", matchesList.get(position).getMember_number());
                chat_sc.putExtra("id", matchesList.get(position).getId());
                chat_sc.putExtra("sender_name", SharedValues.getValue(getActivity(), "sender_name"));
                chat_sc.putExtra("sender_member_id", SharedValues.getValue(getActivity(), "member_id"));
                chat_sc.putExtra("receiver_member_number", matchesList.get(position).getMember_number());
                chat_sc.putExtra("name", matchesList.get(position).getName());
                chat_sc.putExtra("read", matchesList.get(position).getRead());
                startActivity(chat_sc);
            }
        } else if (SharedValues.getValue(getActivity(), "approval_status").equalsIgnoreCase("0")) {
            getredAlert("Your Approval Is Pending \nPlease Contact Admin", "0");
        } else if (SharedValues.getValue(getActivity(), "paid_status").equalsIgnoreCase("0")) {
            getredAlert("Your Membership Payment Is Pending \nPlease Contact Admin", "0");
        } else if (SharedValues.getValue(getActivity(), "subscription_status").equalsIgnoreCase("0")) {
            getredAlert("Your App Subscription Is Pending \nPlease Contact Admin", "1");
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    void getredAlert(String text, final String codition) {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Alert!")
                .setContentText(text)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        if (codition.equalsIgnoreCase("1")) {
                            Intent pay = new Intent(getActivity(), MPayment.class);
                            startActivity(pay);
                        }
                    }
                })
                .show();
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (isOnline(context)) {
                    //dialog(true);
                    Object msg_list = db_tables.getmember_number(SharedValues.getValue(getActivity(), "member_number"));
                    if (msg_list.toString().length() > 0) {
                        if (adapter != null) {
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                        }
                        getStudents();
                    } else {
                        if (adapter != null) {
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                        }
                        recyclerView.removeAllViews();
                        getmemberList();
                    }
                    Log.e("prudhvi", "Online Connect Intenet ");
                } else {
                    // callMessageRaed(false);
                    // dialog(false);
                    if (adapter != null) {
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                    }
                    getmemberList();
                    Toast.makeText(context, "Network connection failed", Toast.LENGTH_SHORT).show();
                   // getredAlert("Network connection failed", "0");
                    Log.e("prudhvi", "Conectivity Failure !!! ");
                }
            } catch (NullPointerException e) {
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
}
