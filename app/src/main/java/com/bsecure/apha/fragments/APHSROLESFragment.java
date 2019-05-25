package com.bsecure.apha.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.R;
import com.bsecure.apha.adapters.APHAListAdapter;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.database.DB_Tables;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.models.MemberModel;
import com.bsecure.apha.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Admin on 2018-06-11.
 */

public class APHSROLESFragment extends ParentFragment implements View.OnClickListener, JsonHandler, APHAListAdapter.ContactAdapterListener {

    private View layout;
    private AccociateMain activity;
    private DB_Tables db_tables;
    String member_number, sender_member_id, receiver_member_ids, district_id, sender_name;
    String action = "0", designation_type;
    private APHAListAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<MemberModel> memberModelArrayList;

    public APHSROLESFragment() {

    }

    public static APHSROLESFragment newInstance() {
        return new APHSROLESFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle mArgs = getArguments();

        if (mArgs != null) {
            designation_type = mArgs.getString("dest_key");
            district_id = mArgs.getString("_id");
        }
        layout = inflater.inflate(R.layout.frg_three, container, false);
        db_tables = new DB_Tables(getActivity());
        db_tables.openDB();
        recyclerView = layout.findViewById(R.id.content_v);
        memberModelArrayList = new ArrayList<>();
        getStudents();

        return layout;
    }

    private void getStudents() {

        try {
            JSONObject object = new JSONObject();
            object.put("designation_type", designation_type);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Processing...", 1, Paths.get_apha_roles, object.toString(), 1);
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
    }

    @Override
    public void onClick(View view) {

    }

    void getredAlert(String text) {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
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

    @Override
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {
                case 1:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray jsonarray2 = object.getJSONArray("designation_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                MemberModel memberModel = new MemberModel();
                                memberModel.setId(jsonobject.optString("designation_id"));
                                memberModel.setName(jsonobject.optString("designation_name"));
                                memberModelArrayList.add(memberModel);
                            }
                            adapter = new APHAListAdapter(memberModelArrayList, getActivity(), this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                        } else {
                            getSweetAlert("No data found");
                        }

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
            activity.getRolesMemberData(matchesList, position,district_id);
        } else if (SharedValues.getValue(getActivity(), "approval_status").equalsIgnoreCase("0")) {
            getredAlert("Your Approval Is Pending \nPlease Contact Admin");
        } else if (SharedValues.getValue(getActivity(), "paid_status").equalsIgnoreCase("0")) {
            getredAlert("Your Membership Payment Is Pending \nPlease Contact Admin");
        } else if (SharedValues.getValue(getActivity(), "subscription_status").equalsIgnoreCase("0")) {
            getredAlert("Your App Subscription Is Pending \nPlease Contact Admin");
        }
    }
}
