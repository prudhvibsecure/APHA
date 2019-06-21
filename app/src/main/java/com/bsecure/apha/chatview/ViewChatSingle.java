package com.bsecure.apha.chatview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.bsecure.apha.R;
import com.bsecure.apha.adapters.MembersAdapter;
import com.bsecure.apha.adapters.MessageListAdapter;
import com.bsecure.apha.callbacks.IDownloadCallback;
import com.bsecure.apha.callbacks.JsonHandler;
import com.bsecure.apha.common.ContentValues;
import com.bsecure.apha.common.Item;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.common.RunTimePermission;
import com.bsecure.apha.database.DB_Tables;
import com.bsecure.apha.https.DownloadService;
import com.bsecure.apha.https.FileUploader;
import com.bsecure.apha.https.HTTPNewPost;
import com.bsecure.apha.models.Download;
import com.bsecure.apha.models.Members;
import com.bsecure.apha.models.MessageObject;
import com.bsecure.apha.provider.CameraPreview;
import com.bsecure.apha.utils.ContactUtils;
import com.bsecure.apha.utils.SharedValues;
import com.bsecure.apha.utils.Utils;
import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by Admin on 2018-11-20.
 */

public class ViewChatSingle extends AppCompatActivity implements View.OnClickListener, JsonHandler, MessageListAdapter.ChatAdapterListener, MembersAdapter.ContactAdapterListener, IDownloadCallback {

    private ImageView mFabButton, mEmoji;
    private RecordButton mAudioButton;
    private RecordView recordView;
    private EmojiconEditText mEditText;
    private RecyclerView mRecyclerView, mMedia;
    private RecyclerView.Adapter mMediaAdapter;
    private RecyclerView.Adapter mVoiceAdapter;
    private MessageListAdapter adapter;
    private static final String SEND_IMAGE = "send_image";
    private static final String MIC_IMAGE = "mic_image";
    public static final String MESSAGE_PROGRESS = "message_progress";
    private EmojIconActions emojIcon;
    private View contentRoot;
    private Dialog member_dialog, pin_diloag, validDialog, timerDiloag, attachDialog, allowDeny, pt_decsDiloag, rep_Dialog;
    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private MediaRecorder mediaRecorder = null;
    private Switch ro_swith, ad_swith, pin_swith, timer_switch, valid_switch, no_forward, no_protection;
    private TextView setPinValue, valid_date_txt, time_value;
    private String rp_sender_member_id, rp_receiver_member_ids, rp_district_id, rp_sender_member_number, rp_sender_name, rp_receiver_member_number;
    private MembersAdapter membersAdapter;

    private CalendarView simpleCalendarView;
    private File sourecFile, file;

    ArrayList<MessageObject> messageList;
    DB_Tables db_tables;
    String name, photUri, number, contactId, mesg_type, mesg_date_time, msg_attach, displayname, attacth_level, filepath, sender_member_number;
    private RunTimePermission runTimePermission;
    private IntentFilter filter, deliver_filter;
    File voicefile_name;

    private ActionMode actionMode;
    private String delete_Id, reply_Id = "", forword_Id, Ids = "", rep_Id = "", rep_msg, rp_name, path_r = "", gt_name;
    ArrayList<String> delete_ids = new ArrayList<>();
    ArrayList<String> reply_ids = new ArrayList<>();

    int count;
    private String msg_fwd, m_types, aName, secure_settings, m_status, restrict_msg, Contact_N;
    boolean conditonLv = false;
    private BroadcastReceiver mNetworkReceiver;
    private TextView chat_last_seeing;
    String mime_type, presedent_id, sender_member_id, district_id, sender_name, receiver_member_number, member_number, dist_id;
    int postion, requestId;
    StringBuilder builder_copy;
    ImageView sec_Tag;
    private String readonce = "0", autodelete = "0", showtime = "0", validuntil = "0", pin = "0", nor_forwrd = "0", no_ptsc = "0", date, pin_default;
    List<Members> contactObjectList;
    String fwd_Id = "", fwd_sId = "";
    ArrayList<String> fr_ids = new ArrayList<>();
    ArrayList<String> fr_ids_l = new ArrayList<>();
    SweetAlertDialog pDialog;
    RecyclerView.LayoutManager mLayoutManager;
    IntentFilter attachFilter;
    private String name_n,read;
    private TextView download_process;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        db_tables = new DB_Tables(this);
        db_tables.openDB();
        download_process = findViewById(R.id.download_process);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        deliver_filter = new IntentFilter("com.chat.app.DELIVER");
        attachFilter = new IntentFilter("com.attach.view");
        filter = new IntentFilter("com.acc.app.BROADCAST_NOTIFICATION");
        filter.setPriority(1);
        Intent getData = getIntent();
        if (getData != null) {
            name_n=getData.getStringExtra("name");
            ((TextView)findViewById(R.id.user_name)).setText(name_n);
            member_number = getData.getStringExtra("member_number");
            presedent_id = getData.getStringExtra("presedent_id");
            district_id = getData.getStringExtra("id");
            sender_member_id = getData.getStringExtra("sender_member_id");
            sender_name = getData.getStringExtra("sender_name");
            receiver_member_number = getData.getStringExtra("receiver_member_number");
            read = getData.getStringExtra("read");
//            condition = getData.getStringExtra("condition");
            if (member_number.equalsIgnoreCase("4")) {
                findViewById(R.id.inputLL).setVisibility(View.GONE);
                findViewById(R.id.record_view).setVisibility(View.GONE);
                findViewById(R.id.send_btn_l).setVisibility(View.GONE);
            }
        }

//        if (member_number.equalsIgnoreCase("4") || member_number.equalsIgnoreCase("1") || member_number.equalsIgnoreCase("2")) {
//            findViewById(R.id.ic_member).setVisibility(View.GONE);
//        } else if (presedent_id.equalsIgnoreCase("2") && member_number.equalsIgnoreCase("3")) {
//            findViewById(R.id.ic_member).setVisibility(View.GONE);
//        } else if (presedent_id.equalsIgnoreCase("1") && member_number.equalsIgnoreCase("5")) {
//            findViewById(R.id.ic_member).setVisibility(View.GONE);
//        } else {
        if (member_number.equalsIgnoreCase("1")&&receiver_member_number.equalsIgnoreCase("2")){
            findViewById(R.id.ic_member).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.ic_member).setVisibility(View.GONE);
        }
        findViewById(R.id.ic_member).setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_view_suer);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = getCurrentItem();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (position > 0) {
                        findViewById(R.id.headerview_ll).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.date_header)).setText(ContactUtils.getTimeAgolatest(Long.parseLong(messageList.get(position).getMessage_date())));
                    } else {
                        findViewById(R.id.headerview_ll).setVisibility(View.GONE);
                    }
                } else if (newState == RecyclerView.SCROLL_INDICATOR_BOTTOM) {
                    if (position > 0) {
                        findViewById(R.id.headerview_ll).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.date_header)).setText(ContactUtils.getTimeAgolatest(Long.parseLong(messageList.get(position).getMessage_date())));
                    } else {
                        findViewById(R.id.headerview_ll).setVisibility(View.GONE);
                    }
                }
            }
        });
        chat_last_seeing = findViewById(R.id.chat_last_seeing);
        findViewById(R.id.user_attach).setOnClickListener(this);
        findViewById(R.id.ic_camera).setOnClickListener(this);
        findViewById(R.id.tap_to_info).setOnClickListener(this);
        findViewById(R.id.user_img).setOnClickListener(this);

        mEmoji = (ImageView) findViewById(R.id.emji_icon);
        contentRoot = findViewById(R.id.contentRoot);
        initViews();
        if (district_id != null) {
            db_tables.updateCount(district_id);
        }
        mNetworkReceiver = new NetworkChangeReceiver();


    }

    private int getCurrentItem() {
        return ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
    }

    private void setCurrentItem(int position, boolean smooth) {
        if (smooth)
            mRecyclerView.smoothScrollToPosition(position);
        else
            mRecyclerView.scrollToPosition(position);
    }

    private void initViews() {
        initMessageBar();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                getChatMessages();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkBroadcastForNougat();
        registerReceiver(mBroadcastReceiver, filter);
        registerReceiver(mBroadcastReceiverDeliver, deliver_filter);
        registerReceiver(mBroadcastReceiverAttach, attachFilter);
        registerReceiver();
    }

    private BroadcastReceiver mBroadcastReceiverAttach = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.attach.view")) {
                download_process.setText("Download completed...");
                download_process.setVisibility(View.GONE);
                String a_name = intent.getStringExtra("attachname");
                String type = Utils.getMimeType(a_name);
                if (type.startsWith("image/") || type.startsWith("audio/") || type.startsWith("video/")) {
                    String path = ContentValues.Images + "/" + a_name.trim();

                    Uri paths = Uri.fromFile(new File(path));
                    Intent intent_n = new Intent(Intent.ACTION_VIEW);
                    intent_n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent_n.setData(Uri.parse(path));
                    intent_n.setDataAndType(paths, type);
                    startActivity(intent_n.createChooser(intent_n, "Open with"));
                } else if (type.startsWith("application/pdf") || type.startsWith("application/msword") || type.startsWith("text/plain")) {
                    try {
                        String file = ContentValues.Images + "/" + a_name.trim();
                        Uri path = Uri.fromFile(new File(file));
                        Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                        launchIntent.setDataAndType(path, type);
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(launchIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "No Application Available to View File", Toast.LENGTH_SHORT).show();
                    }
                } else if (type.startsWith("application/vnd.ms-excel") || type.startsWith("application/vnd.ms-powerpoint") || type.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                    try {
                        String file = ContentValues.Images + "/" + a_name.trim();
                        Uri path = Uri.fromFile(new File(file));
                        Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                        launchIntent.setDataAndType(path, type);
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(launchIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "No Application Available to View File", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    getAlert();
                }
            }
        }
    };
    private void getAlert() {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Alert!")
                .setContentText("Your file download successfully")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();
    }
    private void registerReceiver() {

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
       // bManager.registerReceiver(broadcastReceiverFile, intentFilter);

    }

//    private BroadcastReceiver broadcastReceiverFile = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            if (intent.getAction().equals(MESSAGE_PROGRESS)) {
//
//                Download download = intent.getParcelableExtra("download");
//                if (download.getProgress() == 100) {
//                    // download.get
//                } else {
//
//                }
//            }
//        }
//    };

    private void getChatMessages() {

        try {
            String msg_list = db_tables.getchatList(member_number, district_id);
            messageList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("message_body");

            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    MessageObject messageObject = new MessageObject();

                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    messageObject.setMessage_date(jsonobject.optString("message_date"));
                    messageObject.setMessage(jsonobject.optString("message"));
                    messageObject.setSender_member_id(jsonobject.optString("sender_member_id"));
                    messageObject.setReceiver_member_number(jsonobject.optString("receiver_member_number"));
                    messageObject.setSender_member_number(jsonobject.optString("sender_member_number"));
                    messageObject.setDistrict_id(jsonobject.optString("district_id"));
                    messageObject.setReply_id(jsonobject.optString("reply_id"));
                    messageObject.setSender_name(jsonobject.optString("sender_name"));
                    messageObject.setUser_me(jsonobject.optString("user_me"));
                    messageObject.setMessage_id(jsonobject.optString("message_id"));
                    messageObject.setReceiver_member_ids(jsonobject.optString("receiver_member_ids"));
                    messageObject.setForward_status(jsonobject.optString("forward_status"));
                    messageList.add(messageObject);

                }


                adapter = new MessageListAdapter(messageList, this, this, mediaUriList);
                linearLayoutManager = new LinearLayoutManager(this);
                //linearLayoutManager.setStackFromEnd(true);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                // mRecyclerView.setHasStableIds(true);
                linearLayoutManager.scrollToPosition(messageList.size() - 1);
                mRecyclerView.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMessageBar() {
        mEditText = (EmojiconEditText) findViewById(R.id.send_msg_text);
        emojIcon = new EmojIconActions(this, contentRoot, mEditText, mEmoji);
        emojIcon.ShowEmojIcon();
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    showSendButton();

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sk = s.toString().trim();
                if (sk.length() == 0) {
                    showAudioButton();
                } else {
                    showSendButton();
                }
            }
        });

        mAudioButton = (RecordButton) findViewById(R.id.audio_btn);
        mFabButton = (ImageView) findViewById(R.id.send_btn);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tag = (String) mFabButton.getTag();
                if (tag.equalsIgnoreCase(SEND_IMAGE)) {
                    onSendButtonClicked();
                }

            }
        });
        recordView = (RecordView) findViewById(R.id.record_view);
        mAudioButton.setRecordView(recordView);
        mAudioButton.setListenForRecord(false);
        mAudioButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    getAudioView();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                102);
                    }
                }


//                Toast.makeText(ChatSingle.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
//                Log.d("RecordButton", "RECORD BUTTON CLICKED");
            }
        });

        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        recordView.setCancelBounds(8);
        recordView.setSmallMicColor(Color.parseColor("#c2185b"));
        recordView.setLessThanSecondAllowed(false);

        // recordView.setSlideToCancelText("Slide To Cancel");
        recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {


// Verify that the device has a mic first
                PackageManager pmanager = getPackageManager();
                if (pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
                    // Set the file location for the audio
//                    mFileName = ContentValues.VOICE_FOLDER;
//                    mFileName += DateFormat.format("yyyy-MM-dd", new Date().getTime()) +
//                            "_voice_note.3gp";
                    //File fileDir=new File(mFileName);
                    // sourecFile = ContentValues.createFolders();

//                    File baseDir;
//                    if (Build.VERSION.SDK_INT < 8) {
//                        baseDir = Environment.getExternalStorageDirectory();
//                    } else {
//                        baseDir = Environment.getExternalStorageDirectory();
//                    }
//                    if (baseDir == null) {
//                        baseDir = Environment.getExternalStorageDirectory();
//                    }
//                    File aviaryFolder = new File(baseDir , ContentValues.VOICE_FOLDER);
//
//                    if (!aviaryFolder.exists()) {
//                        success = aviaryFolder.mkdir();
//                    }
//                    String status = Environment.getExternalStorageState();
//                    if(status.equals("mounted")){
//                        String path = ContentValues.VOICE_FOLDER;
//                    }
                    sourecFile = CreateRecord();
                    // Create the recorder
                    mediaRecorder = new MediaRecorder();
                    // Set the audio format and encoder
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                    // Setup the output location
//                    if (sourecFile != null && sourecFile.exists()) {
//                        file = new File(sourecFile, DateFormat.format("yyyy-MM-dd", new Date().getTime()) +
//                                "_voice_note.3gp");
//                    }
                    mediaRecorder.setOutputFile(file.toString());
                    // Start the recording
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                } else { // no mic on device
                    Toast.makeText(getApplicationContext(), "This device doesn't have a mic!", Toast.LENGTH_LONG).show();
                }
//                Log.d("RecordView", "onStart");
//                Toast.makeText(ChatSingle.this, "OnStartRecord", Toast.LENGTH_SHORT).show();
                findViewById(R.id.user_attach).setVisibility(View.GONE);
                findViewById(R.id.ic_camera).setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {

                try {
                    mAudioButton.setListenForRecord(false);
                    new File(file.toString()).delete();
                    findViewById(R.id.user_attach).setVisibility(View.VISIBLE);
                    findViewById(R.id.ic_camera).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish(long recordTime) {

                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                findViewById(R.id.user_attach).setVisibility(View.VISIBLE);
                findViewById(R.id.ic_camera).setVisibility(View.VISIBLE);
                mAudioButton.setListenForRecord(false);
                getHumanTimeText(recordTime);
                voicefile_name = new File(sourecFile.getPath());
                voicefile_name.getName();
                filepath = file.toString();

                String topath = voicefile_name.toString();
                displayname = topath.substring(topath.lastIndexOf("/") + 1);

                Item item = new Item("");
                item.setAttribute("data", filepath);
                item.setAttribute("displayname", displayname);
                item.setAttribute("attachname", displayname);
                startUploadingFile(item);
//                String url = ContentValues.upload_file + "uploadfile1";
//                filepath = nItem.getAttribute("data");
//                FileUploader uploader = new FileUploader(this, this);
//                uploader.setFileName(nItem.getAttribute("displayname"), nItem.getAttribute("attachname"));
//                uploader.userRequest("", 11, url, nItem.getAttribute("data"));
            }

            @Override
            public void onLessThanSecond() {
                try {
                    new File(file.toString()).delete();
                    mAudioButton.setListenForRecord(false);
                    recordView.setSoundEnabled(false);
                    findViewById(R.id.user_attach).setVisibility(View.VISIBLE);
                    findViewById(R.id.ic_camera).setVisibility(View.VISIBLE);
                    if (mediaRecorder != null) {
                        mediaRecorder.stop();
                        mediaRecorder.reset();
                        mediaRecorder.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                // Log.d("RecordView", "Basket Animation Finished");
            }
        });
    }

    private void getAudioView() {
        if (mAudioButton.isListenForRecord()) {
            mAudioButton.setListenForRecord(false);


        } else {
            mAudioButton.setListenForRecord(true);
            Toast toast = Toast.makeText(getApplicationContext(), "Tap to record,hold to release", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 100, 150);
            View view = toast.getView();
            TextView tv_txt = (TextView) view.findViewById(android.R.id.message);
            tv_txt.setTypeface(Typeface.SANS_SERIF);
            tv_txt.setPadding(10, 5, 10, 5);
            tv_txt.setTextColor(Color.WHITE);
            view.setBackgroundResource(R.color.black);
            toast.show();

        }
    }

    private void sendVoiceMessage() {

        try {


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void showSendButton() {
        findViewById(R.id.user_attach).setVisibility(View.GONE);
        findViewById(R.id.ic_camera).setVisibility(View.GONE);
        findViewById(R.id.audio_btn).setVisibility(View.GONE);
        findViewById(R.id.send_btn).setVisibility(View.VISIBLE);
        // mFabButton.setImageResource(R.mipmap.ic_send_btn);
        mFabButton.setTag(SEND_IMAGE);
    }

    private void showAudioButton() {
        findViewById(R.id.user_attach).setVisibility(View.VISIBLE);
        findViewById(R.id.ic_camera).setVisibility(View.VISIBLE);
        findViewById(R.id.send_btn).setVisibility(View.GONE);
        findViewById(R.id.audio_btn).setVisibility(View.VISIBLE);
        // mFabButton.setImageResource(R.mipmap.ic_microphone);
        mFabButton.setTag(MIC_IMAGE);

    }

    private void onSendButtonClicked() {
        String message = mEditText.getText().toString();
        mEditText.setText("");
        if (reply_Id.isEmpty()) {
            writeTextMessage(message);
        } else {
            writeRepandAll(message);
        }
    }

    private void writeRepandAll(String message) {
        try {
            findViewById(R.id.mesg_fr).setVisibility(View.GONE);
            findViewById(R.id.ic_member).setVisibility(View.GONE);
            String mesg_date_time = String.valueOf(System.currentTimeMillis());
            String sender_member_number = SharedValues.getValue(this, "member_number");
            db_tables.messageData(message, null, mesg_date_time, sender_member_id, district_id, sender_member_number, receiver_member_number, sender_name, "0");
            db_tables.UpdateRpId(mesg_date_time, reply_Id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.disableProgress();
            if (requestId == 1) {
                JSONObject object = new JSONObject();
                object.put("message", message);
                object.put("message_date", mesg_date_time);
                object.put("sender_member_id", sender_member_id);
                object.put("receiver_member_ids", rp_receiver_member_ids);
                object.put("district_id", district_id);
                object.put("sender_member_number", sender_member_number);
                object.put("receiver_member_number", receiver_member_number);
                object.put("sender_name", sender_name);
                object.put("reply_id", reply_Id);
                task.userRequest("", 1, Paths.reply_message, object.toString(), 1);
            } else {
                JSONObject object = new JSONObject();
                object.put("message", message);
                object.put("message_date", mesg_date_time);
                object.put("sender_member_id", sender_member_id);
                if (fr_ids.size() > 0) {
                    object.put("receiver_member_ids", rp_receiver_member_ids);
                } else {
                    object.put("receiver_member_ids", "");
                }
                object.put("district_id", district_id);
                object.put("sender_member_number", sender_member_number);
                object.put("receiver_member_number", receiver_member_number);
                object.put("sender_name", sender_name);
                object.put("reply_id", reply_Id);
                task.userRequest("", 1, Paths.reply_to_all_message, object.toString(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeTextMessage(String msg) {
        try {
            msg = msg.trim();
            if (msg.length() == 0) {
                return;
            } else {
                mesg_date_time = String.valueOf(System.currentTimeMillis());
//                message, message_date, sender_member_id, receiver_member_ids, district_id,
//                        sender_member_number, receiver_member_number, sender_name
                sender_member_number = SharedValues.getValue(this, "member_number");
                JSONObject object = new JSONObject();
                object.put("message", msg);
                object.put("message_date", mesg_date_time);
                object.put("sender_member_id", sender_member_id);
                object.put("receiver_member_ids", "");
                object.put("district_id", district_id);
                object.put("sender_member_number", sender_member_number);
                object.put("receiver_member_number", receiver_member_number);
                object.put("sender_name", sender_name);
                object.put("attatach_orgname", displayname);
                db_tables.messageData(msg, null, mesg_date_time, sender_member_id, district_id, sender_member_number, receiver_member_number, sender_name, "0");
               // db_tables.updateMemberList(district_id, msg, presedent_id);
                HTTPNewPost task = new HTTPNewPost(this, this);
                task.disableProgress();
                task.userRequest("", 1, Paths.send_message, object.toString(), 1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_attach:

                if (reply_Id.isEmpty()) {
                    checkPermission();
                } else {
                    checkPermission();
                }
                //openGallery();
                break;
            case R.id.ic_camera:

                viewCameraTag();

                break;

            case R.id.remove_ll_msg:
                reply_Id = "";
                findViewById(R.id.mesg_fr).setVisibility(View.GONE);
                findViewById(R.id.ic_member).setVisibility(View.VISIBLE);
                break;
            case R.id.ic_member:
                fr_ids = null;
                fr_ids = new ArrayList<>();
//                if (presedent_id.equalsIgnoreCase("1")) {
//                    getMembersList();
//                } else if (presedent_id.equalsIgnoreCase("2")) {
                getMembersList();
//                }
                break;
            case R.id.send_for:
                if (fr_ids.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (String s : fr_ids_l) {
                        builder.append("," + s);
                    }
                    String fis = builder.toString();
                    rp_receiver_member_ids = fis.substring(1);
                    member_dialog.dismiss();

                }
                break;
        }

    }

    private void getMembersList() {
        contactObjectList = new ArrayList<>();
        member_dialog = new Dialog(this, R.style.MyAlertDialogStyle);
        member_dialog.setContentView(R.layout.members_list);
        member_dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        member_dialog.setCancelable(true);
        member_dialog.setCanceledOnTouchOutside(true);
        member_dialog.findViewById(R.id.send_for).setOnClickListener(this);
        member_dialog.getWindow().setGravity(Gravity.BOTTOM);
        getListDataMember();
        member_dialog.show();
    }


    private void getListDataMember() {
        try {
            showDialog();
            JSONObject object = new JSONObject();
            object.put("member_number", presedent_id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.disableProgress();

            task.userRequest("Processing...", 4, Paths.get_members, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void dismisDialog() {
        pDialog.dismiss();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
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

    private void viewCameraTag() {

        runTimePermission = new RunTimePermission(this);
        runTimePermission.requestPermission(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, new RunTimePermission.RunTimePermissionListener() {

            @Override
            public void permissionGranted() {
                // First we need to check availability of play services
                Intent chatview = new Intent(getApplicationContext(), CameraPreview.class);
                startActivityForResult(chatview, 222);
                // startActivity(new Intent(ChatSingle.this, CameraPreview.class));

            }

            @Override
            public void permissionDenied() {

                finish();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, 2798);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 2798:

                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        //showToast(R.string.fnf);
                        return;
                    }
                    validateSelectedFile(processFileMetadata(data));
                }
                break;
            case 222:

                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    String PATH = data.getStringExtra("PATH");
                    String THUMB = data.getStringExtra("THUMB");
                    String WHO = data.getStringExtra("WHO");

                    //String topath = PATH.toString();
                    displayname = THUMB.substring(THUMB.lastIndexOf("/") + 1);
                    filepath = PATH;
                    mime_type = Utils.getMimeType(filepath);
                    Item item = new Item("");
                    item.setAttribute("data", filepath);
                    item.setAttribute("displayname", displayname);
                    item.setAttribute("attachname", displayname);
                    startUploadingFile(item);

                }
                break;
        }
    }

    // private void startUploadingFile(Item nItem) {

//        String url = ContentValues.upload_file + "uploadfile1";
//        filepath = nItem.getAttribute("data");
//        FileUploader uploader = new FileUploader(this, this);
//        uploader.setFileName(nItem.getAttribute("displayname"), nItem.getAttribute("attachname"));
//        uploader.userRequest("", 11, url, nItem.getAttribute("data"));
    //  }


    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter = null;
        unregisterNetworkChanges();
        unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(mBroadcastReceiverDeliver);
        unregisterReceiver(mBroadcastReceiverAttach);
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (intent.getAction().equals("com.acc.app.BROADCAST_NOTIFICATION")) {
                    getChatMessages();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public File CreateRecord() {
        File tempPicFile = null;
        String ext_storage_state = Environment.getExternalStorageState();
        File mediaStorage = new File(Environment.getExternalStorageDirectory() + ContentValues.VOICE_FOLDERs);
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            } else {
                //do nothing
            }
            tempPicFile = new File(mediaStorage.getPath() + File.separator
                    + DateFormat.format("yyyyMMdd_HHmmss", new Date().getTime()) + "_voice_note.mp3");

            file = tempPicFile;

        } else {

        }
        return tempPicFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (runTimePermission != null) {
            runTimePermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (requestCode == 101
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // getAtachDiloag();
            openGallery();
        }
        if (requestCode == 102
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAudioView();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private final BroadcastReceiver mBroadcastReceiverDeliver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (intent.getAction().equals("com.chat.app.DELIVER")) {
                    String contat_Id = intent.getStringExtra("contactId");
                    String message_timestamp = intent.getStringExtra("message_timestamp");
                    String message_id = intent.getStringExtra("message_id");
                    // if (contat_Id != null) {
                    // getUpdateDeliver(message_timestamp, message_id);
                    // }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onBackPressed() {
        reply_Id = "";
        delete_Id = "";
        rp_receiver_member_ids = "";
        fwd_Id = "";
        fwd_sId = "";
        forword_Id = "";
        sendBrodcast();
        super.onBackPressed();
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

        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), null, sel,
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

            if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {

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
                        cursor = getContentResolver().query(contentUri, null, null, null, null);
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
                cursor = getContentResolver().query(uri, null, null, null, null);

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
                cursor = getContentResolver().query(uri, null, null, null, null);

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
                    cursor = getContentResolver().query(uri, null, null, null, null);
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
            if (fileSize > 25000000) {
                //showToast(R.string.fsitbpsafwilt);
                return;
            }

            String attachTime = Utils.getDeviceDateTime("yyyyMMdd_hhmmss");
            displayname = item.getAttribute("displayname");
            msg_attach = displayname;
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

            mime_type = Utils.getMimeType(filepath);
//            Item item = new Item("");
//            item.setAttribute("data", file.toString());
//            item.setAttribute("displayname", displayname);
//            item.setAttribute("attachname", displayname);
            startUploadingFile(item);
//            db_tables.messageData(msg, null, mesg_date_time, sender_member_id, district_id, sender_member_number, receiver_member_number, sender_name, "0");
//            db_tables.updateMemberList(mesg_date_time, msg, presedent_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startUploadingFile(Item nItem) {

        String url = Paths.base + "members/upload_photo";
        filepath = nItem.getAttribute("data");
        FileUploader uploader = new FileUploader(this, this);
        uploader.setFileName(nItem.getAttribute("displayname"), nItem.getAttribute("attachname"));
        uploader.userRequest("", 11, url, nItem.getAttribute("data"));
    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int requestId) {
        try {

            switch (what) {

                case -1: // failed

//                    View fview = attachmentLayout.findViewById(requestId);
//                    Item fitem = (Item) fview.getTag();
//
//                    removeAttachment(requestId);

                    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();

//                    fitem = null;
//                    fview = null;

                    // uploadingIds.remove(requestId);

                    break;

                case 1: // progressBar


//                    View pview = attachmentLayout.findViewById(requestId);
//
//                    ((ProgressControl) pview.findViewById(R.id.download_progress)).updateProgressState((Long[]) obj);// setText(fullItem.getAttribute("displayname"));

                    break;

                case 0: // success
                    //sendImage();
                    JSONObject object = new JSONObject(obj.toString());
                    //     {"status":"0","status_description":"File Uploaded Successfully","attachname":"1552318451_Screenshot_20181203-194010_20190311_090349.png"}
//                    if (mime_type == null) {
                    writeTextMessage(object.optString("attachname"));
//                    } else if (mime_type.startsWith("video/")) {
//                        writeTextMessage(object.optString("attachname"));
//                    } else if (mime_type.startsWith("application/")) {
//                        writeTextMessage(object.optString("attachname"));
//                    } else {
//                        writeTextMessage(object.optString("attachname"));
//                    }
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void sendImage() {

    }


    @SuppressLint("WrongConstant")
    private void copyText() {

        ((ClipboardManager) ViewChatSingle.this.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", builder_copy.toString()));
    }


    private void replyMessage() {
        try {
            adapter.resetAnimationIndex();
            for (int i = 0; i < reply_ids.size(); i++) {
                String d_ids = reply_ids.get(i);
                reply_Id = d_ids;
            }
            if (reply_Id.trim().length() == 0)
                return;
            //reply_Id = reply_Id.substring(1);
            sendReplyMesg(reply_Id, rep_msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteMessage(int vale) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendReplyMesg(String reply_id, String rep_msg) {


    }

    private void registerNetworkBroadcastForNougat() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//
//        }
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // }

    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {
                case 1:
                    reply_Id = "";
                    fwd_Id = "";
                    fwd_sId = "";
                    forword_Id = "";
                    fr_ids = null;
                    fr_ids = new ArrayList<>();
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {

                        db_tables.UserMessage(object.optString("message_date"), object.optString("message_id"));


//                        if (mesg_type.equalsIgnoreCase("2")) {
//
//
//                            Item item = new Item("");
//                            item.setAttribute("data", file.toString());
//                            item.setAttribute("displayname", displayname);
//                            item.setAttribute("attachname", displayname);
//                            startUploadingFile(item);
//
//                        }
                        getChatMessages();
                        Intent intent = new Intent();
                        intent.setAction("com.acc.app.BROADCAST_NOTIFICATION");
                        intent.putExtra("NotificationData", "0");
                        intent.putExtra("receiver_member_number", "");
                        sendBroadcast(intent);
                    }
                    break;
                case 2:
                    reply_Id = "";
                    conditonLv = false;
                    //status allow and deny
                    break;
                case 4:
                    dismisDialog();
                    JSONObject object_v = new JSONObject(results.toString());
                    if (object_v.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray jsonarray2 = object_v.getJSONArray("member_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                Members contact = new Members();
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                contact.setMember_id(jsonobject.getString("member_id"));
                                contact.setMember_name(jsonobject.getString("member_name"));
                                contact.setMember_number(jsonobject.getString("member_number"));
                                contact.setProfile_image(jsonobject.getString("profile_image"));
                                contact.setReg_mobile_no(jsonobject.getString("reg_mobile_no"));
                                contactObjectList.add(contact);
                            }

                            RecyclerView recyclerView = member_dialog.findViewById(R.id.member_list);
                            mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, true);
                            recyclerView.setLayoutManager(mLayoutManager);
                            membersAdapter = new MembersAdapter(contactObjectList, this, this);
                            recyclerView.setAdapter(membersAdapter);
                        } else {
                            member_dialog.findViewById(R.id.n_data).setVisibility(View.VISIBLE);
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

    @Override
    public void onMessageRowClicked(int position, String contactName, String contact_name, String pic_url, String contact_phone) {

    }

    @Override
    public void onMessageShow(List<MessageObject> matchesList, int position) {
        String my_type = Utils.getMimeType(matchesList.get(position).getMessage());
        if (my_type.startsWith("video/")) {
            getAttachmentview(matchesList, position);
        } else if (my_type.startsWith("image/")) {
            getAttachmentview(matchesList, position);
        } else if (my_type.startsWith("application/")) {
            getAttachmentview(matchesList, position);
        } else {

        }
    }

    @Override
    public void onMessageDocs(List<MessageObject> matchesList, int position) {
        String my_type = Utils.getMimeType(matchesList.get(position).getMessage());
        if (my_type.startsWith("video/")) {
            getAttachmentview(matchesList, position);
        } else if (my_type.startsWith("image/")) {
            getAttachmentview(matchesList, position);
        } else if (my_type.startsWith("application/")) {
            getAttachmentview(matchesList, position);
        } else {

        }
    }

    @Override
    public void onMessageImages(List<MessageObject> matchesList, int position) {
        String my_type = Utils.getMimeType(matchesList.get(position).getMessage());
        if (my_type.startsWith("video/")) {
            getAttachmentview(matchesList, position);
        } else if (my_type.startsWith("image/")) {
            getAttachmentview(matchesList, position);
        } else if (my_type.startsWith("application/")) {
            getAttachmentview(matchesList, position);
        } else {

        }
    }

    @Override
    public void onMessageView(List<MessageObject> matchesList, int position) {
        String my_type = Utils.getMimeType(matchesList.get(position).getMessage());
        if (my_type.startsWith("video/")) {
            getAttachmentview(matchesList, position);
        } else if (my_type.startsWith("image/")) {
            getAttachmentview(matchesList, position);
        } else if (my_type.startsWith("application/")) {
            getAttachmentview(matchesList, position);
        } else {

        }

    }

    private void getAttachmentview(List<MessageObject> matchesList, int position) {
        File mediaStorage = new File(ContentValues.Images);
        if (!mediaStorage.exists()) {
            mediaStorage.mkdirs();
        }
        String path = ContentValues.Images + "/" + matchesList.get(position).getMessage().trim();

        File file = new File(path);
        if (file.exists()) {
            String type = Utils.getMimeType(path);
            if (type.startsWith("image/") || type.startsWith("audio/") || type.startsWith("video/")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(path));
                intent.setDataAndType(Uri.parse(path), type);
                startActivity(Intent.createChooser(intent, "Open with"));
            } else if (type.startsWith("application/pdf") || type.startsWith("application/msword") || type.startsWith("application/vnd.ms-powerpoint") || type.startsWith("application/vnd.ms-excel") || type.startsWith("text/plain") || type.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                try {
                    Uri paths = Uri.fromFile(new File(path));
                    Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                    launchIntent.setDataAndType(paths, type);
                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(launchIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "No Application Available to View File", Toast.LENGTH_SHORT).show();
                }
            } else {
                Uri paths = Uri.fromFile(new File(path));
                Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                launchIntent.setDataAndType(paths, "application/*");
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(launchIntent);
            }
            return;
            //Do something
        } else {
            download_process.setVisibility(View.VISIBLE);
            download_process.setText("Downloading...");
            //Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();
            startService(DownloadService.getDownloadService(this, Paths.up_load, String.valueOf(mediaStorage), matchesList.get(position).getMessage().trim()));
            return;
        }
    }

    @Override
    public void onRowLongClicked(List<MessageObject> matchesList, int position, boolean value) {
        String m_Id = SharedValues.getValue(this, "member_number");
       // repForDiloag(matchesList, position);

    }

    private void repForDiloag(final List<MessageObject> matchesList, final int position) {


        rep_Dialog = new Dialog(this, R.style.MyAlertDialogStyle);
        rep_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View dialogView = View.inflate(this, R.layout.reply_forword_ll, null);
        rep_Dialog.setContentView(dialogView);
        rep_Dialog.setCancelable(true);
        rep_Dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rep_Dialog.getWindow().setGravity(Gravity.BOTTOM);

        String m_Id = SharedValues.getValue(this, "member_number");
        if (m_Id.equals(matchesList.get(position).getSender_member_number())) {
            if (matchesList.get(position).getForward_status().equalsIgnoreCase("1")) {
                rep_Dialog.findViewById(R.id.rep_1).setVisibility(View.GONE);
                rep_Dialog.findViewById(R.id.rep_11).setVisibility(View.VISIBLE);
                rep_Dialog.findViewById(R.id.forward_msg).setVisibility(View.GONE);
            } else {
                return;
            }
        } else if (m_Id.equalsIgnoreCase("3")) {
            rep_Dialog.findViewById(R.id.forward_msg).setVisibility(View.GONE);
        } else if (m_Id.equalsIgnoreCase("5")) {
            rep_Dialog.findViewById(R.id.forward_msg).setVisibility(View.GONE);
        } else {
            rep_Dialog.findViewById(R.id.rep_1).setVisibility(View.VISIBLE);
            rep_Dialog.findViewById(R.id.rep_11).setVisibility(View.GONE);
            rep_Dialog.findViewById(R.id.forward_msg).setVisibility(View.VISIBLE);

        }
        rep_Dialog.findViewById(R.id.rep_1).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        replyMsg(1, matchesList, position);
                    }
                });
        rep_Dialog.findViewById(R.id.rep_11).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyMsg(2, matchesList, position);
                    }
                });
        rep_Dialog.findViewById(R.id.forward_msg).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getForward(matchesList.get(position).getMessage(), matchesList.get(position).getMessage_id());
                        rep_Dialog.dismiss();
                    }
                });
        rep_Dialog.show();
    }

    private void getForward(String message, String msgId) {
        try {

            String mesg_date_time = String.valueOf(System.currentTimeMillis());
            String sender_member_number = SharedValues.getValue(this, "member_number");
            JSONObject object = new JSONObject();
            object.put("message", message);
            object.put("message_date", mesg_date_time);
            object.put("sender_member_id", sender_member_id);
            object.put("receiver_member_ids", "");
            object.put("district_id", district_id);
            object.put("sender_member_number", sender_member_number);
            object.put("receiver_member_number", receiver_member_number);
            object.put("sender_name", sender_name);
            object.put("message_id", msgId);
            db_tables.messageData(message, null, mesg_date_time, sender_member_id, district_id, sender_member_number, receiver_member_number, sender_name, "0");
            db_tables.updateForword(mesg_date_time);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.disableProgress();
            task.userRequest("", 1, Paths.forward_message, object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //        message, message_date, sender_member_id,
//                receiver_member_ids, district_id,
//                sender_member_number, receiver_member_number, sender_name, reply_id
    private void replyMsg(int reqId, List<MessageObject> matchesList, int position) {

        try {
            rep_Dialog.dismiss();
            findViewById(R.id.mesg_fr).setVisibility(View.VISIBLE);
            if (matchesList.get(position).getForward_status().equals("1")) {
                findViewById(R.id.ic_member).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.ic_member).setVisibility(View.GONE);
            }
            findViewById(R.id.remove_ll_msg).setOnClickListener(this);
            ((TextView) findViewById(R.id.user_nnmae_add)).setText(matchesList.get(position).getSender_name());
            ((TextView) findViewById(R.id.mesg_reps_v)).setText(matchesList.get(position).getMessage());
            if (!TextUtils.isEmpty(path_r)) {
                Glide.with(this).load(path_r).into(((ImageView) findViewById(R.id.send_img_ll)));
                ((TextView) findViewById(R.id.mesg_reps_v)).setText("Attachment Added");
            }
            reply_Id = matchesList.get(position).getMessage_id();
            rp_sender_member_id = matchesList.get(position).getSender_member_id();
            rp_receiver_member_ids = matchesList.get(position).getReceiver_member_ids();
            rp_district_id = matchesList.get(position).getDistrict_id();
            rp_sender_member_number = matchesList.get(position).getSender_member_number();
            rp_sender_name = matchesList.get(position).getSender_name();
            //receiver_member_number = matchesList.get(position).getReceiver_member_number();
            requestId = reqId;
            rp_receiver_member_ids = rp_sender_member_id;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRowClicked(List<Members> matchesList, boolean value, CheckBox chk_name, int position) {
        if (chk_name.isChecked()) {
            mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, true);
            fwd_Id = matchesList.get(position).getMember_name();
            fr_ids.remove(fwd_Id);
            fwd_sId = matchesList.get(position).getMember_id();
            fr_ids_l.remove(fwd_sId);
            chk_name.setChecked(false);
            chk_name.setVisibility(View.GONE);

            if (fr_ids.size() > 0) {
                member_dialog.findViewById(R.id.list_fwd).setVisibility(View.VISIBLE);
                ((TextView) member_dialog.findViewById(R.id.text_contcats)).setText(fr_ids.toString());
            } else {
                member_dialog.findViewById(R.id.list_fwd).setVisibility(View.GONE);
            }
        } else {
            mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, true);
            fwd_Id = matchesList.get(position).getMember_name();
            fwd_sId = matchesList.get(position).getMember_id();
            // fwd_Id = matchesList.get(position).get();
            fr_ids.add(fwd_Id);
            fr_ids_l.add(fwd_sId);
            chk_name.setChecked(true);
            chk_name.setVisibility(View.VISIBLE);
            member_dialog.findViewById(R.id.list_fwd).setVisibility(View.VISIBLE);
            ((TextView) member_dialog.findViewById(R.id.text_contcats)).setText(fr_ids.toString());

        }
    }


    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (isOnline(context)) {
                    //dialog(true);

                    Log.e("prudhvi", "Online Connect Intenet ");
                } else {
                    // callMessageRaed(false);
                    // dialog(false);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                sendBrodcast();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendBrodcast() {
        Intent intent = new Intent();
        intent.setAction("com.acc.app.BROADCAST_NOTIFICATION");
        intent.putExtra("NotificationData", "1");
        intent.putExtra("receiver_member_number", receiver_member_number);
        sendBroadcast(intent);
    }
}
