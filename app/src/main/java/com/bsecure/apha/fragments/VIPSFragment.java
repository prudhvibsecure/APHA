package com.bsecure.apha.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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
import com.bsecure.apha.controls.ColorGenerator;
import com.bsecure.apha.controls.TextDrawable;
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

public class VIPSFragment extends ParentFragment implements APHAListAdapter.ContactAdapterListener, JsonHandler {

    private View layout;

    private AccociateMain activity;

    private RecyclerView recyclerView;
    private APHAListAdapter adapter;
    private ArrayList<MemberModel> memberModelArrayList;
    private Dialog m_dialog;
    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private String _id, name;

    public VIPSFragment() {
        // Required empty public constructor
    }

    public static VIPSFragment newInstance() {
        return new VIPSFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle mArgs = getArguments();

        if (mArgs != null) {
            _id = mArgs.getString("id");
            name = mArgs.getString("name");
        }
        layout = inflater.inflate(R.layout.frg_two, container, false);
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();
        recyclerView = layout.findViewById(R.id.vip_list);
        memberModelArrayList = new ArrayList<>();

        getVipList();

        return layout;
    }

    private void getVipList() {

        HTTPNewPost task = new HTTPNewPost(getActivity(), this);
        task.disableProgress();
        task.userRequest("Processing...", 1, Paths.get_sector_list, "", 1);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AccociateMain) context;
    }

    @Override
    public void onMessageRowClicked(List<MemberModel> matchesList, int position) {

        if (SharedValues.getValue(getActivity(), "approval_status").equalsIgnoreCase("0")) {
            getredAlert("Your Approval Is Pending - Please Contact Admin");
        } else {
            //else if (SharedValues.getValue(getActivity(), "paid_status").equalsIgnoreCase("0")) {
//            getredAlert("Your Membership Payment Is Pending - Please Contact Admin");
//        } else if (SharedValues.getValue(getActivity(), "subscription_status").equalsIgnoreCase("0")) {
//            getredAlert("Your App Subscription Is Pending");
//        } else {
            activity.showSectors(matchesList, _id, position);
        }

       // }
    }

    void getredAlert(String text) {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
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
                        JSONArray jsonarray2 = object.getJSONArray("sector_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                MemberModel memberModel = new MemberModel();
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);

                                memberModel.setId(jsonobject.optString("sector_id"));
                                memberModel.setName(jsonobject.optString("sector_name"));
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

    @Override
    public void onFailure(String errorCode, int requestType) {

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
}
