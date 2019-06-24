package com.bsecure.apha.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.CustomDialogue;
import com.bsecure.apha.R;
import com.bsecure.apha.adapters.APMEMListAdapter;
import com.bsecure.apha.adapters.MembersNewAdapter;
import com.bsecure.apha.callbacks.IDownloadCallback;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.chatview.ChatSingle;
import com.bsecure.apha.common.Item;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.controls.ColorGenerator;
import com.bsecure.apha.controls.TextDrawable;
import com.bsecure.apha.https.FileUploader;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.models.Members;
import com.bsecure.apha.models.VipModel;
import com.bsecure.apha.otp.WebPage;
import com.bsecure.apha.utils.SharedValues;
import com.bsecure.apha.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Admin on 2018-06-11.
 */

public class MembersChatFragment extends ParentFragment implements MembersNewAdapter.ContactAdapterListener, JsonHandler, View.OnClickListener, IDownloadCallback {

    private View layout;

    private AccociateMain activity;

    private RecyclerView recyclerView;
    private MembersNewAdapter adapter;
    private ArrayList<Members> vipModelArrayList;
    private Dialog m_dialog;
    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    String designation_id, district_id,member_id="",member_number="",member_name="",d_id="",filepath;
    private EditText msg_text;
    Dialog dialog;
    private String displayname="";
    public MembersChatFragment() {
        // Required empty public constructor
    }

    public static MembersChatFragment newInstance() {
        return new MembersChatFragment();
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


        getVipList();

        return layout;
    }

    private void getVipList() {
        try {
            JSONObject object = new JSONObject();
            object.put("phone_number", SharedValues.getValue(getActivity(), "reg_mobile_no"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            if (task.isNetworkAvailable()) {
                task.userRequest("Processing...", 1, Paths.view_user_members, object.toString(), 1);
            }
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
                        JSONArray jsonarray = object.getJSONArray("user_details");
                        if (jsonarray.length() > 0) {
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                Members vipModel = new Members();
                                vipModel.setMember_id(jsonobject.optString("member_id"));
                                vipModel.setMember_name(jsonobject.optString("member_name"));
                                vipModel.setMember_number(jsonobject.optString("member_number"));
                                vipModel.setDistrict_id(jsonobject.optString("district_id"));
                                vipModelArrayList.add(vipModel);

                            }
                            adapter = new MembersNewAdapter(vipModelArrayList, getActivity(), this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                        } else {
                            getSweetAlert("No data found");
                        }
                    }
                    break;
                case 2:
                    displayname = null;
                    dialog.dismiss();
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
    public void onRowClicked(List<Members> matchesList, boolean value, CheckBox chk_name, int position) {
        dialog= new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogue);
        dialog.findViewById(R.id.send_btn).setOnClickListener(this);
        msg_text = dialog.findViewById(R.id.send_msg_text);
        dialog.findViewById(R.id.user_attach).setOnClickListener(this);
        member_id=matchesList.get(position).getMember_id();
        member_number   =matchesList.get(position).getMember_number();
        member_name   =matchesList.get(position).getMember_name();
        d_id   =matchesList.get(position).getDistrict_id();
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:
                String msg = msg_text.getText().toString();
                if (msg.length() == 0) {
                    return;
                }
                getSndMsg(msg);
                break;
            case R.id.user_attach:
                checkPermission();
                break;
        }

    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, 2798);
    }
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // getAtachDiloag();
            openGallery();
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
            // getAtachDiloag();
            openGallery();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    private void getSndMsg(String msg) {
        try {
            String mesg_date_time = String.valueOf(System.currentTimeMillis());
            JSONObject object = new JSONObject();
            object.put("message", msg);
            object.put("message_date", mesg_date_time);
            object.put("sender_member_id", member_id);
            object.put("receiver_member_ids", "");
            object.put("district_id", d_id);
            object.put("sender_member_number", member_number);
            object.put("receiver_member_number", "4");
            object.put("sender_name", member_name);
            object.put("attatach_orgname", displayname);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.disableProgress();
            task.userRequest("", 2, Paths.send_message, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2798:

                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    validateSelectedFile(processFileMetadata(data));
                }
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Item processFileMetadata(Intent intent) {

        String url = intent.getDataString();

        // writeLogs("URL : " + url);

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String id = "0";

        url = url.replace("content://", "");

        if (url.contains(":"))
            id = url.substring(url.lastIndexOf(":") + 1, url.length());
        else
            id = url.substring(url.lastIndexOf("/") + 1, url.length());

        //writeLogs("URL after : " + url);
        //writeLogs("id after : " + id);

        String sel = MediaStore.Video.Media._ID + "=?";

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Files.getContentUri("external"), null, sel,
                new String[]{id}, null);

        // writeLogs("cursor : " + cursor);

        Item item = null;

        if (cursor != null && cursor.moveToFirst()) {
            item = new Item("");

            String[] resultsColumns = cursor.getColumnNames();
            do {

                for (int i = 0; i < resultsColumns.length; i++) {
                    String key = resultsColumns[i];
                    String value = cursor.getString(cursor.getColumnIndexOrThrow(resultsColumns[i]));

                    // writeLogs("key : " + key + "       ---   " + "value : " + value);

                    if (value != null) {
                        if (key.contains("_"))
                            key = key.replace("_", "");
                        item.setAttribute(key, value);

                    }
                }

            } while (cursor.moveToNext());

            cursor.close();
            cursor = null;

        }

        if (item == null) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            Uri uri = intent.getData();

            if (isKitKat && DocumentsContract.isDocumentUri(getActivity(), uri)) {

                if (isExternalStorageDocument(uri)) {

                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
//                        return Environment.getExternalStorageDirectory() +
//                         "/" + split[1];
                        String path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        item = new Item("");
                        item.setAttribute("path", Environment.getExternalStorageDirectory() + "/" + split[1]);
                        String filename = path.substring(path.lastIndexOf("/") + 1);
                        item.setAttribute("displayname", filename);
                        item.setAttribute("size", String.valueOf((int) new File((String) item.get("path")).length()));
                        item.setAttribute("data", path);

                    }
                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                if (isDownloadsDocument(uri)) {

                    final String documentId = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(documentId));

                    try {
                        cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            item = new Item("");
                            String[] resultsColumns = cursor.getColumnNames();

                            for (int i = 0; i < resultsColumns.length; i++) {
                                String key = resultsColumns[i];
                                String value = cursor.getString(cursor.getColumnIndexOrThrow(resultsColumns[i]));

                                // Log.e("" + key, value + "");

                                if (value != null) {
                                    if (key.contains("_"))
                                        key = key.replace("_", "");
                                    item.setAttribute(key, value);

                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }

                }

            }
            if (isNewGooglePhotosUri(uri)) {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    item = new Item("");
                    do {
                        String[] resultsColumns = cursor.getColumnNames();
                        for (int i = 0; i < resultsColumns.length; i++) {
                            String key = resultsColumns[i];

                            if (key.equalsIgnoreCase("com.google.android.apps.photos.contentprovider"))
                                key = "_id";

                            String value = cursor.getString(cursor.getColumnIndexOrThrow(
                                    key/* resultsColumns[i] */));
                            if (value != null) {
                                if (key.contains("_"))
                                    key = key.replace("_", "");
                                item.setAttribute(key, value);

                            }
                        }

                    } while (cursor.moveToNext());

                    cursor.close();
                    cursor = null;
                }
            }
            if (isGoogleUri(uri)) {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    item = new Item("");
                    do {
                        String[] resultsColumns = cursor.getColumnNames();
                        for (int i = 0; i < resultsColumns.length; i++) {
                            String key = resultsColumns[i];

                            if (key.equalsIgnoreCase("com.google.android.apps.docs.storage.legacy"))
                                key = "_id";

                            String value = cursor.getString(cursor.getColumnIndexOrThrow(
                                    key/* resultsColumns[i] */));
                            if (value != null) {
                                if (key.contains("_"))
                                    key = key.replace("_", "");
                                item.setAttribute(key, value);

                            }
                        }

                    } while (cursor.moveToNext());

                    cursor.close();
                    cursor = null;
                }
            }
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                String path = uri.getPath();

                item = new Item("");
                item.setAttribute("path", path);
                String filename = path.substring(path.lastIndexOf("/") + 1);
                item.setAttribute("displayname", filename);
                item.setAttribute("size", String.valueOf((int) new File((String) item.get("path")).length()));
                item.setAttribute("data", path);

            }
            if (gDrvie(uri)) {
                String path = String.valueOf(uri);//getPath();
                try {
                    cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        item = new Item("");
                        String[] resultsColumns = cursor.getColumnNames();

                        for (int i = 0; i < resultsColumns.length; i++) {
                            String key = resultsColumns[i];
                            String value = cursor.getString(cursor.getColumnIndexOrThrow(resultsColumns[i]));

                            // Log.e("" + key, value + "");

                            if (value != null) {
                                if (key.contains("_"))
                                    key = key.replace("_", "");
                                item.setAttribute(key, value);
                                item.setAttribute("data", path);

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null)
                        cursor.close();
                }

            }
        }

        return item;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isNewGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

    public static boolean isGoogleUri(Uri uri) {
        return "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    public static boolean gDrvie(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    private void validateSelectedFile(final Item item) {
        try {
            if (item == null) {
                // showToast(R.string.syhnsai1);
                return;
            }
            long fileSize = 0;
            fileSize = Long.parseLong(item.getAttribute("size")); // 6808826
            if (fileSize > 100000000) {
                //showToast(R.string.fsitbpsafwilt);
                return;
            }

            String attachTime = Utils.getDeviceDateTime("yyyyMMdd_hhmmss");
            displayname = item.getAttribute("displayname");
           // msg_attach = displayname;
            if (displayname.contains(".")) {
                String[] temp = displayname.split("\\.");
                if (temp.length > 2) {
                    displayname = temp[0] + temp[1] + "_" + attachTime + "." + temp[temp.length - 1];
                } else {
                    displayname = temp[0] + "_" + attachTime + "." + temp[1];
                }
            } else {
                displayname = displayname + "_" + attachTime;

            }
            displayname = displayname.replaceAll("\\s+", "");
            item.setAttribute("attachname", displayname);
            filepath = item.getAttribute("data");
            startUploadingFile(item);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void startUploadingFile(Item nItem) {
        String url = Paths.base + "members/upload_photo";
        filepath = nItem.getAttribute("data");
        FileUploader uploader = new FileUploader(getActivity(), this);
        uploader.setFileName(nItem.getAttribute("displayname"), nItem.getAttribute("attachname"));
        uploader.userRequest("", 11, url, nItem.getAttribute("data"));
    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int requestId) {
        try {

            switch (what) {

                case -1: // failed

//
                    Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();

//                    fitem = null;
//                    fview = null;

                    // uploadingIds.remove(requestId);

                    break;

                case 1: // progressBar

                   // findViewById(R.id.progress_ll).setVisibility(View.VISIBLE);
//                    View pview = attachmentLayout.findViewById(requestId);
//
//                    ((ProgressControl) pview.findViewById(R.id.download_progress)).updateProgressState((Long[]) obj);// setText(fullItem.getAttribute("displayname"));

                    break;

                case 0: // success
                    //sendImage();
                    JSONObject object = new JSONObject(obj.toString());
                    //     {"status":"0","status_description":"File Uploaded Successfully","attachname":"1552318451_Screenshot_20181203-194010_20190311_090349.png"}
//                    if (mime_type == null) {

                    getSndMsg(object.optString("attachname"));

                    break;

                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
