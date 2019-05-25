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
import android.widget.Switch;
import android.widget.TextView;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.R;
import com.bsecure.apha.adapters.ApproveDoneAdapter;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.controls.ColorGenerator;
import com.bsecure.apha.controls.TextDrawable;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.models.Members;
import com.bsecure.apha.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Admin on 2018-06-11.
 */

public class ApprovelFragment extends ParentFragment implements ApproveDoneAdapter.ContactAdapterListener, JsonHandler {

    private View layout;

    private AccociateMain activity;

    private RecyclerView recyclerView;
    private ApproveDoneAdapter adapter;
    private ArrayList<Members> vipModelArrayList;
    private Dialog m_dialog;
    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private boolean isViewShown = false;
    public ApprovelFragment() {
        // Required empty public constructor
    }

    public static ApprovelFragment newInstance() {
        return new ApprovelFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.frg_two, container, false);
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();
        recyclerView = layout.findViewById(R.id.vip_list);
        vipModelArrayList = new ArrayList<>();
        if (!isViewShown) {
            getVipList();
        }


        return layout;
    }

    private void getVipList() {

        try {
            JSONObject object = new JSONObject();
            object.put("district_id", SharedValues.getValue(getActivity(), "district_id"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.disableProgress();
            task.userRequest("Processing...", 1, Paths.list_members_by_district, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AccociateMain) context;
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
                        layout.findViewById(R.id.counter).setVisibility(View.VISIBLE);
                        ((TextView)layout.findViewById(R.id.counter)).setText("Approved Members: "+object.optString("approve_count"));
                        JSONArray jsonarray = object.getJSONArray("approve_memb_details");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            Members vipModel = new Members();
                            vipModel.setMember_id(jsonobject.optString("members_id"));
                            vipModel.setMember_name(jsonobject.optString("member_name"));
                            vipModel.setApproval_status(jsonobject.optString("approval_status"));
                            vipModel.setReg_mobile_no(jsonobject.optString("reg_mobile_no"));
                            vipModel.setProfile_image(jsonobject.optString("profile_image"));
                            vipModel.setBusiness_name(jsonobject.optString("business_name"));
                            vipModel.setAdded_date(jsonobject.optString("added_date"));
                            vipModelArrayList.add(vipModel);

                        }
                        adapter = new ApproveDoneAdapter(vipModelArrayList, getActivity(), this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);
                    }else { layout.findViewById(R.id.counter).setVisibility(View.GONE);
                        adapter.clear();
                        adapter.notifyDataSetChanged();

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

    @Override
    public void onRowClicked(List<Members> matchesList, boolean value, Switch chk_name, int position) {

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
            if (adapter==null) {
                getVipList();
            }
        } else {
            isViewShown = false;
        }
    }
}
