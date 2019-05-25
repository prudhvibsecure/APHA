package com.bsecure.apha.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.R;
import com.bsecure.apha.adapters.VipListAdapter;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.controls.ColorGenerator;
import com.bsecure.apha.controls.TextDrawable;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.models.VipModel;
import com.bsecure.apha.utils.SharedValues;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ShowVIPFragment extends ParentFragment implements VipListAdapter.ContactAdapterListener, JsonHandler {

    private View layout;

    private AccociateMain activity;

    private RecyclerView recyclerView;
    private VipListAdapter adapter;
    private ArrayList<VipModel> vipModelArrayList;
    private Dialog m_dialog;
    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    String sector_id, district_id, name;

    public ShowVIPFragment() {
        // Required empty public constructor
    }

    public static ShowVIPFragment newInstance() {
        return new ShowVIPFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle mArgs = getArguments();

        if (mArgs != null) {
            sector_id = mArgs.getString("sector_id");
            district_id = mArgs.getString("district_id");
            name = mArgs.getString("name");
        }
        layout = inflater.inflate(R.layout.frg_two, container, false);
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();
        recyclerView = layout.findViewById(R.id.vip_list);
        vipModelArrayList = new ArrayList<>();

        getVipList();

        return layout;
    }

    private void getVipList() {

        try {
            JSONObject object = new JSONObject();
            object.put("sector_id", sector_id);
            object.put("district_id", district_id);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Processing...", 1, Paths.get_vip_members, object.toString(), 1);
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
    public void onMessageRowClicked(List<VipModel> matchesList, int position) {

        if (SharedValues.getValue(getActivity(), "approval_status").equalsIgnoreCase("0")) {
            getredAlert("Your Approval Is Pending \nPlease Contact Admin");
        } else {
            int color = generator.getColor(matchesList.get(position).getVip_name());
            TextDrawable ic1 = builder.build(matchesList.get(position).getVip_name().substring(0, 1).trim(), color);
            m_dialog = new Dialog(getActivity(), R.style.MyAlertDialogStyle);
            m_dialog.setContentView(R.layout.vip_view_data);
            m_dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            m_dialog.setCancelable(true);
            m_dialog.setCanceledOnTouchOutside(true);
            m_dialog.findViewById(R.id.user_profile_desi).setVisibility(View.VISIBLE);
            ((LinearLayout) m_dialog.findViewById(R.id.user_profile_short_bio1)).setVisibility(View.GONE);
            ((TextView) m_dialog.findViewById(R.id.user_profile_name)).setText(matchesList.get(position).getVip_name());
            ((TextView) m_dialog.findViewById(R.id.email)).setText(matchesList.get(position).getPhone_number());
            final String number = matchesList.get(position).getPhone_number();
            m_dialog.findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent call = new Intent(Intent.ACTION_CALL);
                    call.setData(Uri.parse("tel:" + number));
//                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
//                    startActivity(call);
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE},
                                200);
                    } else {
                        //You already have permission
                        try {
                            startActivity(call);
                        } catch(SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            ((TextView) m_dialog.findViewById(R.id.user_profile_desi)).setText(matchesList.get(position).getInformation());
            ((TextView) m_dialog.findViewById(R.id.design)).setVisibility(View.VISIBLE);
            ((TextView) m_dialog.findViewById(R.id.design)).setText("Designation : " + matchesList.get(position).getDesignation());
            ((TextView) m_dialog.findViewById(R.id.statet)).setText(matchesList.get(position).getDistrict() + ", " + matchesList.get(position).getState());
            ((TextView) m_dialog.findViewById(R.id.distet)).setText(matchesList.get(position).getState());

            if (matchesList.get(position).getImage().isEmpty()) {
                ((ImageView) m_dialog.findViewById(R.id.user_profile_photo)).setImageDrawable(ic1);
            } else {
                String path = Paths.up_load + matchesList.get(position).getImage();
                Glide.with(this).load(path).diskCacheStrategy(DiskCacheStrategy.SOURCE).into((ImageView) m_dialog.findViewById(R.id.user_profile_photo));
            }
            m_dialog.getWindow().setGravity(Gravity.TOP);
            m_dialog.show();
        }
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
                        JSONArray jsonarray = object.getJSONArray("vip_details");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            VipModel vipModel = new VipModel();

                            vipModel.setVips_id(jsonobject.optString("vips_id"));
                            vipModel.setVip_name(jsonobject.optString("vip_name"));
                            vipModel.setDesignation(jsonobject.optString("designation_name"));
                            vipModel.setPhone_number(jsonobject.optString("phone_number"));
                            vipModel.setInformation(jsonobject.optString("information"));
                            vipModel.setSector(jsonobject.optString("sector_name"));
                            vipModel.setState(jsonobject.optString("state"));
                            vipModel.setDistrict(jsonobject.optString("district"));
                            vipModel.setImage(jsonobject.optString("profile_image"));
                            vipModelArrayList.add(vipModel);

                        }
                        adapter = new VipListAdapter(vipModelArrayList, getActivity(), this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        getSweetAlert("No data found");
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
                        sDialog.dismiss();
                        activity.onKeyDown(4, null);
                    }
                })
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the phone call

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}