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
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.R;
import com.bsecure.apha.adapters.APMEMListAdapter;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.controls.ColorGenerator;
import com.bsecure.apha.controls.TextDrawable;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.models.VipModel;
import com.bsecure.apha.otp.WebPage;
import com.bsecure.apha.utils.SharedValues;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Admin on 2018-06-11.
 */

public class APHMembersFragment extends ParentFragment implements APMEMListAdapter.ContactAdapterListener, JsonHandler {

    private View layout;

    private AccociateMain activity;

    private RecyclerView recyclerView;
    private APMEMListAdapter adapter;
    private ArrayList<VipModel> vipModelArrayList;
    private Dialog m_dialog;
    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    String designation_id, district_id,name;
    String[] websites;
    private String desc ="";
    ListView list;
    ArrayAdapter<String>wadapter;

    public APHMembersFragment() {
        // Required empty public constructor
    }

    public static APHMembersFragment newInstance() {
        return new APHMembersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle mArgs = getArguments();

        if (mArgs != null) {
            designation_id = mArgs.getString("dest_key");
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
            object.put("designation_id", designation_id);
            object.put("district_id", district_id);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.disableProgress();
            task.userRequest("Processing...", 1, Paths.get_designation_members, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFragmentName() {
        return name;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AccociateMain) context;
    }

    @Override
    public void onMessageRowClicked(List<VipModel> matchesList, int position) {

        if (SharedValues.getValue(getActivity(), "approval_status").equalsIgnoreCase("0")) {
            getredAlert("Your Approval Is Pending - Please Contact Admin");
        } else if (SharedValues.getValue(getActivity(), "paid_status").equalsIgnoreCase("0")) {
            getredAlert("Your Membership Payment Is Pending - Please Contact Admin");
        } else if (SharedValues.getValue(getActivity(), "subscription_status").equalsIgnoreCase("0")) {
            getredAlert("Your App Subscription Is Pending");
        } else {
            getmemberData(matchesList.get(position).getVips_id());
        }
    }

    private void getmemberData(String member_id) {
        try {
            JSONObject object = new JSONObject();
            object.put("members_id", member_id);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Processing...", 2, Paths.get_member, object.toString(), 1);
        } catch (Exception e) {

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
                        JSONArray jsonarray = object.getJSONArray("member_details");
                        if (jsonarray.length() > 0) {
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                VipModel vipModel = new VipModel();

                                vipModel.setVips_id(jsonobject.optString("members_id"));
                                vipModel.setVip_name(jsonobject.optString("member_name"));
                                vipModel.setPhone_number(jsonobject.optString("reg_mobile_no"));
                                vipModel.setImage(jsonobject.optString("profile_image"));
                                vipModelArrayList.add(vipModel);

                            }
                            adapter = new APMEMListAdapter(vipModelArrayList, getActivity(), this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                        } else {
                            getSweetAlert("No data found");
                        }
                    }
                    break;
                case 2:
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray jsonarray = object1.getJSONArray("member_details");
                        if (jsonarray.length() > 0) {
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(0);
                                int color = generator.getColor(jsonobject.optString("member_name"));
                                TextDrawable ic1 = builder.build(jsonobject.optString("member_name").substring(0, 1).trim(), color);
                                m_dialog = new Dialog(getActivity(), R.style.MyAlertDialogStyle);
                                m_dialog.setContentView(R.layout.vip_view_data);
                                m_dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                m_dialog.setCancelable(true);
                                m_dialog.setCanceledOnTouchOutside(true);
                                ((TextView) m_dialog.findViewById(R.id.design)).setVisibility(View.VISIBLE);
                                ((TextView) m_dialog.findViewById(R.id.design)).setText("Designation : " + jsonobject.optString("designation_name"));
                                ((TextView) m_dialog.findViewById(R.id.user_profile_name)).setText(jsonobject.optString("member_name"));
                                ((TextView) m_dialog.findViewById(R.id.email)).setText(jsonobject.optString("reg_mobile_no"));
                                final String number = jsonobject.optString("reg_mobile_no");
                                m_dialog.findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent call = new Intent(Intent.ACTION_CALL);
                                        call.setData(Uri.parse("tel:" + number));
//                                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                                            return;
//                                        }
//                                        startActivity(call);
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
                                String bus_name = jsonobject.optString("business_name");
                                String bb_arr[] = bus_name.split(",");
                                for (String text_bus : bb_arr) {
                                    TextView answerInputField = new TextView(getActivity());
                                    answerInputField.setText(text_bus);
                                    answerInputField.setTextColor(Color.WHITE);
                                    answerInputField.setGravity(Gravity.LEFT);
                                    answerInputField.setLayoutParams(
                                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));

                                    ((LinearLayout) m_dialog.findViewById(R.id.user_profile_short_bio)).addView(answerInputField);


                                }
                                // ((TextView) m_dialog.findViewById(R.id.user_profile_short_bio)).setText();
                                ((TextView) m_dialog.findViewById(R.id.statet)).setText(jsonobject.optString("district") + ", " + jsonobject.optString("state"));
                                ((TextView) m_dialog.findViewById(R.id.distet)).setVisibility(View.VISIBLE);
                                final String url = jsonobject.optString("website");
                                if (url == null || url.isEmpty()) {
                                    ((TextView) m_dialog.findViewById(R.id.distet)).setText(Html.fromHtml("<u>No Website</u>"));
                                } else {
                                    websites = jsonobject.optString("website").split(",");
                                    list = m_dialog.findViewById(R.id.list);
                                    wadapter = new ArrayAdapter<String>(getActivity(), R.layout.website, R.id.text, websites);
                                    list.setAdapter(wadapter);
                                    wadapter.notifyDataSetChanged();

                                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            getpksview(websites[position]);
                                        }
                                    });
                                    /*for(String s:websites) {
                                        desc = desc+s+"<br>";
                                    }
                                    desc = desc.substring(0,desc.length()-4);*/
                                    //((TextView) m_dialog.findViewById(R.id.distet)).setText(Html.fromHtml("<u>" + jsonobject.optString("website") + "</u>"));
                                    //((TextView) m_dialog.findViewById(R.id.distet)).setText(Html.fromHtml(desc));
                                }
                                /*((TextView) m_dialog.findViewById(R.id.distet)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (url == null || url.isEmpty()) {
                                            return;
                                        } else {
                                            getpksview(url);

                                        }

                                    }
                                });*/
                                ((TextView) m_dialog.findViewById(R.id.mail)).setVisibility(View.VISIBLE);
                                if(jsonobject.optString("email_id").isEmpty())
                                {
                                    ((TextView) m_dialog.findViewById(R.id.mail)).setText(jsonobject.optString("email"));
                                }
                                else
                                {
                                    ((TextView) m_dialog.findViewById(R.id.mail)).setText("No Email ID");
                                }
                                if (jsonobject.optString("profile_image").isEmpty()) {
                                    ((ImageView) m_dialog.findViewById(R.id.user_profile_photo)).setImageDrawable(ic1);
                                } else {
                                    String path = Paths.up_load + jsonobject.optString("profile_image");
                                    Glide.with(this).load(path).diskCacheStrategy(DiskCacheStrategy.SOURCE).into((ImageView) m_dialog.findViewById(R.id.user_profile_photo));
                                }
                                //((ImageView) m_dialog.findViewById(R.id.user_profile_photo)).setImageDrawable(ic1);
                                m_dialog.getWindow().setGravity(Gravity.CENTER);
                                m_dialog.show();
                            }
                        } else {
                            getSweetAlert("No data found");
                        }
                    }
                    break;
            }

        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

    }

    private void getpksview(String url) {
        Intent sendIntent = new Intent(getActivity(), WebPage.class);
        sendIntent.putExtra("url", url);
        getActivity().startActivity(sendIntent);

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
    public void onFailure(String errorCode, int requestType) {

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
