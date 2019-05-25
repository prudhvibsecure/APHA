package com.bsecure.apha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.apha.callbacks.IDownloadCallback;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.fragments.ParentFragment;
import com.bsecure.apha.https.FileUploader;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.utils.SharedValues;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileView extends ParentFragment implements JsonHandler, IDownloadCallback {

    ImageView contact_image;
    private String imagepath = null, fileName = "", ex_v;
    private Bitmap bitmap = null;
    private Uri uri;
    String pic_name;
    private View layout;
    private AccociateMain activity;
    private IntentFilter spFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.profile_status, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolset);
        toolbar.setVisibility(View.GONE);
//        toolbar.setTitle("Profile");
//        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        spFilter=new IntentFilter("com.profile.action");
        contact_image = layout.findViewById(R.id.pf_img);
        contact_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1000);
            }
        });
        pic_name = SharedValues.getValue(getActivity(), "user_pic");
        if (pic_name.isEmpty()) {

        } else {
            String f_path = Paths.up_load + pic_name;
            Glide.with(this).load(f_path).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contact_image);
        }
//        String mem_num = SharedValues.getValue(getActivity(), "member_number");
//        if (mem_num.equalsIgnoreCase("1")) {
//            ((TextView) layout.findViewById(R.id.profile_dest)).setText("State President");
//        } else if (mem_num.equalsIgnoreCase("2")) {
//            ((TextView) layout.findViewById(R.id.profile_dest)).setText("District President");
//        } else if (mem_num.equalsIgnoreCase("3")) {
//            ((TextView) layout.findViewById(R.id.profile_dest)).setText("District EC");
//        } else if (mem_num.equalsIgnoreCase("5")) {
//            ((TextView) layout.findViewById(R.id.profile_dest)).setText("State EC");
//        } else {
            ((TextView) layout.findViewById(R.id.profile_dest)).setText(SharedValues.getValue(getActivity(),"designation_name"));
//        }

        ((EditText) layout.findViewById(R.id.u_name)).setText(SharedValues.getValue(getActivity(), "sender_name"));
        ((EditText) layout.findViewById(R.id.u_name)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) layout.findViewById(R.id.u_name)).setCursorVisible(true);
            }
        });
        ((EditText) layout.findViewById(R.id.u_mobile)).setText(SharedValues.getValue(getActivity(), "reg_mobile_no"));
        ((EditText) layout.findViewById(R.id.u_bus_n)).setText(SharedValues.getValue(getActivity(), "business_name"));
        String values_area = SharedValues.getValue(getActivity(), "district") + ", " + SharedValues.getValue(getActivity(), "state");
        if (values_area != null)
            ((EditText) layout.findViewById(R.id.state_city)).setText(values_area);
        layout.findViewById(R.id.submt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateProfile();
            }
        });
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBroadProfile,spFilter);
    }

    private BroadcastReceiver mBroadProfile=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.profile.action")){
                ((EditText) layout.findViewById(R.id.u_name)).setText(SharedValues.getValue(getActivity(), "sender_name"));
                ((EditText) layout.findViewById(R.id.u_bus_n)).setText(SharedValues.getValue(getActivity(), "business_name"));
                String values_area = SharedValues.getValue(getActivity(), "district") + ", " + SharedValues.getValue(getActivity(), "state");
                if (values_area != null)
                    ((EditText) layout.findViewById(R.id.state_city)).setText(values_area);
            }
        }
    };
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AccociateMain) context;
    }

    @Override
    public String getFragmentName() {
        return "Profile";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadProfile);
    }

    private void updateProfile() {

        String u_name = ((EditText) layout.findViewById(R.id.u_name)).getText().toString();
        if (u_name.length() == 0) {
            getError("Please enter name");
            return;
        }
        String u_bus_n = ((EditText) layout.findViewById(R.id.u_bus_n)).getText().toString();
        if (u_bus_n.length() == 0) {
            getError("Please enter business name");
            return;
        }
        try {

            JSONObject object = new JSONObject();
            object.put("member_name", u_name);
            object.put("business_name", u_bus_n);
            object.put("profile_image", fileName);
            object.put("reg_mobile_no", SharedValues.getValue(getActivity(), "reg_mobile_no"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Processing...", 1, Paths.update_member, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getError(String text) {

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
                        SharedValues.saveValue(getActivity(), "sender_name", object.optString("member_name"));
                        SharedValues.saveValue(getActivity(), "business_name", object.optString("business_name"));
                        SharedValues.saveValue(getActivity(), "user_pic", pic_name);
                        Toast.makeText(getActivity(), object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                        activity.onKeyDown(4, null);
                    }
                    break;
                default:
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            if (resultCode == getActivity().RESULT_OK) {
                JSONObject object = processData(data);

                if (object == null) {
                    Toast.makeText(getActivity(), "Please select any file", Toast.LENGTH_SHORT).show();
                    return;
                }

                imagepath = object.optString("data");

                fileName = object.optString("displayname");

                bitmap = decodeFile(new File(imagepath), 2);
                //rotateImage(bitmap, imagepath);

                if (bitmap != null) {
                    bitmap = getRoundedShape(bitmap);
                    //Bitmap fbitmap = getMyRotation(imagepath);
                    contact_image.setImageBitmap(bitmap);
                    startUploadingFile();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Bitmap decodeFile(File f, int photoToLoad) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            int REQUIRED_SIZE = 70;

            if (photoToLoad == 1)
                REQUIRED_SIZE = 120;

            if (photoToLoad == 2 || photoToLoad == 5)
                REQUIRED_SIZE = 200;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 200;
        int targetHeight = 200;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2, ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth), ((float) targetHeight)) / 2), Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    private JSONObject processData(Intent intent) {
        try {

            if (intent == null) {
                return null;
            }

            uri = intent.getData();
            if (uri == null) {
                return null;
            }

            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                JSONObject object = new JSONObject();
                do {
                    String[] resultsColumns = cursor.getColumnNames();
                    for (int i = 0; i < resultsColumns.length; i++) {
                        String key = resultsColumns[i];

                        if (key.equalsIgnoreCase("com.google.android.apps.photos.api.special_type_id"))
                            key = "_id";

                        String value = cursor.getString(cursor.getColumnIndexOrThrow(
                                key/* resultsColumns[i] */));

                        if (value != null) {
                            if (key.contains("_"))
                                key = key.replace("_", "");
                            object.put(key, value);
                        }
                        imagepath = object.optString("data");
                        SharedValues.saveValue(getActivity(), "contact_pic", imagepath);
                        //getMyRotation(imagepath, uri);
                    }

                } while (cursor.moveToNext());

                cursor.close();
                cursor = null;

                return object;
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    private void startUploadingFile() {

        String url = Paths.base + "members/upload_photo";

        FileUploader uploader = new FileUploader(getActivity(), this);
        uploader.setFileName(fileName, fileName);
        uploader.userRequest("", 11, url, imagepath);
    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int requestId) {
        try {
            switch (what) {

                case -1: // failed

                    Toast.makeText(getActivity(), "faild", Toast.LENGTH_SHORT).show();
                    break;

                case 1: // progressBar


//                    View pview = attachmentLayout.findViewById(requestId);
//
//                    ((ProgressControl) pview.findViewById(R.id.download_progress)).updateProgressState((Long[]) obj);// setText(fullItem.getAttribute("displayname"));

                    break;

                case 0: // success
                    //  updateProfile();
                    JSONObject object = new JSONObject(obj.toString());

                    pic_name = object.optString("attachname");
                    //Toast.makeText(this, object.toString(), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
