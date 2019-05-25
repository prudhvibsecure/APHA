package com.bsecure.apha.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import com.bsecure.apha.R;
import com.bsecure.apha.common.Paths;
import com.bsecure.apha.database.DB_Tables;
import com.bsecure.apha.models.MessageObject;
import com.bsecure.apha.models.UserRepo;
import com.bsecure.apha.utils.ContactUtils;
import com.bsecure.apha.utils.SharedValues;
import com.bsecure.apha.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ContactViewHolder> {

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ChatAdapterListener listener;
    ArrayList<String> mediaList;
    private List<MessageObject> matchesList;
    MediaPlayer mediaPlayer = new MediaPlayer();
    boolean wasPlaying = false;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private static int currentSelectedIndex = -1;

    public MessageListAdapter(List<MessageObject> list, Context context, ChatAdapterListener listener, ArrayList<String> mediaList) {
        this.context = context;
        this.listener = listener;
        this.matchesList = list;
        this.mediaList = mediaList;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (matchesList.size() == 0) {
                arr = 0;

            } else {
                arr = matchesList.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @Override
    public void onBindViewHolder(final ContactViewHolder contactViewHolder, int position) {

        try {
            DB_Tables db_tables = new DB_Tables(context);
            db_tables.openDB();
            final MessageObject chatList = matchesList.get(position);
            contactViewHolder.my_header_date.setText(ContactUtils.getTimeAgolatest(Long.parseLong(chatList.getMessage_date())));
            if (position > 0) {
                String date_one = Utils.getDate(Long.parseLong(matchesList.get(position).getMessage_date()));
                String date_two = Utils.getDate(Long.parseLong(matchesList.get(position - 1).getMessage_date()));
                if (date_one.equals(date_two)) {
                    contactViewHolder.d_header.setVisibility(View.GONE);
                } else {
                    contactViewHolder.d_header.setVisibility(View.VISIBLE);
                }
            } else {
                contactViewHolder.d_header.setVisibility(View.VISIBLE);
            }
            String chk_v = chatList.getSender_member_number();
            if (chk_v.equals(SharedValues.getValue(context, "member_number"))) {
                if (chatList.getReply_id().isEmpty()) {
                    String m_type = Utils.getMimeType(chatList.getMessage());
                    if (m_type == null) {
                        contactViewHolder.show_icon.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
                        contactViewHolder.txtMessage_send.setText(chatList.getMessage());
                        contactViewHolder.uu_name.setText(chatList.getSender_name());
                        contactViewHolder.uu_name.setVisibility(View.VISIBLE);
                        if (chatList.getForward_status().equalsIgnoreCase("1")) {
                            contactViewHolder.fwText.setVisibility(LinearLayout.VISIBLE);
                        } else {
                            contactViewHolder.fwText.setVisibility(LinearLayout.GONE);
                        }
                        contactViewHolder.timestamp_send.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                    } else if (m_type.startsWith("image/")) {
                        //   contactViewHolder.show_icon.setVisibility(View.VISIBLE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.VISIBLE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        contactViewHolder.vi_play_l.setVisibility(View.GONE);
                        if (chatList.getForward_status().equalsIgnoreCase("1")) {
                            contactViewHolder.fd_img_rt.setVisibility(View.VISIBLE);
                        } else {
                            contactViewHolder.fd_img_rt.setVisibility(View.GONE);
                        }

                        Glide.with(context).load(Paths.up_load + chatList.getMessage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.txtMessage_send_img);


                        // String date = df.format(Calendar.getInstance().getTime());
                        //Date currentTime = Calendar.getInstance().getTime();
                        contactViewHolder.timestamp_send_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        // contactViewHolder.show_icon.setVisibility(View.VISIBLE);
                    } else if (m_type.startsWith("video/")) {
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.VISIBLE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.VISIBLE);
                        if (chatList.getForward_status().equalsIgnoreCase("1")) {
                            contactViewHolder.fd_img_rt.setVisibility(View.VISIBLE);
                        } else {
                            contactViewHolder.fd_img_rt.setVisibility(View.GONE);
                        }

                       // Glide.with(context).load(Paths.up_load + chatList.getMessage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.txtMessage_send_img);
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

                        mediaMetadataRetriever .setDataSource(Paths.up_load + chatList.getMessage(), new HashMap<String, String>());
                        Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(1000);
                        contactViewHolder.txtMessage_send_img.setImageBitmap(bmFrame);
                        contactViewHolder.timestamp_send_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                    } else if (m_type.startsWith("audio/")) {
                        contactViewHolder.vo_sent.setVisibility(View.GONE);
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.VISIBLE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        //String date = df.format(Calendar.getInstance().getTime());
                        contactViewHolder.timestamp_vc_send.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        final String uri = Paths.up_load + chatList.getMessage();
                        if (chatList.getForward_status().equalsIgnoreCase("1")) {
                            contactViewHolder.fd_voice_lt.setVisibility(View.VISIBLE);
                        } else {
                            contactViewHolder.fd_voice_lt.setVisibility(View.GONE);
                        }
                        contactViewHolder.ply_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {


                                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                        clearMediaPlayer();
                                        contactViewHolder.seekBar.setProgress(0);
                                        wasPlaying = true;
                                        contactViewHolder.ply_btn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
                                    }


                                    if (!wasPlaying) {

                                        if (mediaPlayer == null) {
                                            mediaPlayer = new MediaPlayer();
                                        }

                                        contactViewHolder.ply_btn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.pause_btn));

                                        //AssetFileDescriptor descriptor = context.getAssets().openFd(uri.toString());
                                        mediaPlayer.setDataSource(uri.toString());

                                        mediaPlayer.prepare();
                                        mediaPlayer.setVolume(0.5f, 0.5f);
                                        mediaPlayer.setLooping(false);
                                        contactViewHolder.seekBar.setMax(mediaPlayer.getDuration());

                                        mediaPlayer.start();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int currentPosition = mediaPlayer.getCurrentPosition();
                                                int total = mediaPlayer.getDuration();


                                                while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        currentPosition = mediaPlayer.getCurrentPosition();
                                                    } catch (InterruptedException e) {
                                                        return;
                                                    } catch (Exception e) {
                                                        return;
                                                    }

                                                    contactViewHolder.seekBar.setProgress(currentPosition);

                                                }
                                            }
                                        }).start();

                                    }

                                    wasPlaying = false;
                                } catch (Exception e) {
                                    clearMediaPlayer();
                                    contactViewHolder.seekBar.setProgress(0);
                                    wasPlaying = true;
                                    contactViewHolder.ply_btn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
                                    e.printStackTrace();

                                }
                                //Toast.makeText(context, "under process", Toast.LENGTH_SHORT).show();
                            }
                        });

                        contactViewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                                contactViewHolder.timer_t.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                                contactViewHolder.timer_t.setVisibility(View.VISIBLE);
                                int x = (int) Math.ceil(progress / 1000f);

                                if (x < 10)
                                    contactViewHolder.timer_t.setText("0:0" + x);
                                else
                                    contactViewHolder.timer_t.setText("0:" + x);

                                double percent = progress / (double) seekBar.getMax();
                                int offset = seekBar.getThumbOffset();
                                int seekWidth = seekBar.getWidth();
                                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                                int labelWidth = contactViewHolder.timer_t.getWidth();
                                contactViewHolder.timer_t.setX(offset + seekBar.getX() + val
                                        - Math.round(percent * offset)
                                        - Math.round(percent * labelWidth / 2));

                                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                                    clearMediaPlayer();
                                    contactViewHolder.ply_btn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
                                    seekBar.setProgress(0);
                                }

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {


                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                    mediaPlayer.seekTo(seekBar.getProgress());
                                }
                            }
                        });
                    } else {

                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.VISIBLE);
                        contactViewHolder.doc_name.setText(chatList.getMessage());
                        // String date = df.format(Calendar.getInstance().getTime());
                        contactViewHolder.timestamp_doc_sent.setText(getDate(Long.parseLong(chatList.getMessage_date())));
//                        if (chatList.getFile_name().endsWith(".pdf") || chatList.getFile_name().endsWith(".Pdf") || chatList.getFile_name().endsWith(".PDF")) {
//                            contactViewHolder.doc_img.setImageResource(R.mipmap.ic_act_pdf);
//                        } else {
//                            contactViewHolder.doc_img.setImageResource(R.mipmap.ic_action_doc);
//                        }
                        if (chatList.getForward_status().equalsIgnoreCase("1")) {
                            contactViewHolder.fd_doc_rt.setVisibility(View.VISIBLE);
                        } else {
                            contactViewHolder.fd_doc_rt.setVisibility(View.GONE);
                        }
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.vi_play_l.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                    }
                } else {
                    ArrayList<UserRepo> rep_msg = db_tables.getRepMessage(chatList.getReply_id());
                    String m_type = Utils.getMimeType(chatList.getMessage());
                    if (m_type == null) {
                        contactViewHolder.reply_l1.setVisibility(View.VISIBLE);
                        contactViewHolder.leftMsgLayout.setVisibility(LinearLayout.GONE);
                        contactViewHolder.mesg_reps_send.setText(rep_msg.get(0).getMessage());
                        contactViewHolder.txtMessage_send_reply.setText(chatList.getMessage());
                        contactViewHolder.user_nnmae_rep_send.setText(chatList.getSender_name());
                        contactViewHolder.timestamp_send_reply.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.uu_name.setVisibility(View.GONE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                    } else if (m_type.startsWith("image/")) {
                        String miType = rep_msg.get(0).getMessage();
                        if (miType == null) {
                            contactViewHolder.reps_send_img_txt.setText(rep_msg.get(0).getMessage());
                            contactViewHolder.r_img_rep_send.setText(rep_msg.get(0).getName());
                            contactViewHolder.rep_send_img_d.setVisibility(View.GONE);
                        } else if (miType.startsWith("image/")) {
                            contactViewHolder.reps_send_img_txt.setText("Photo");
                            contactViewHolder.rep_send_img_d.setVisibility(View.VISIBLE);
                            contactViewHolder.r_img_rep_send.setText(rep_msg.get(0).getName());
                            Glide.with(context).load(rep_msg.get(0).getPath()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.rep_send_img_d);
                        } else if (miType.startsWith("audio/")) {
                            contactViewHolder.reps_send_img_txt.setText("Voice");
                            contactViewHolder.r_img_rep_send.setText(rep_msg.get(0).getName());
                            contactViewHolder.rep_send_img_d.setImageResource(R.mipmap.voice_msg);
                        } else if (miType.startsWith("video/")) {
                            contactViewHolder.reps_send_img_txt.setText("Video");
                            contactViewHolder.r_img_rep_send.setText(rep_msg.get(0).getName());
                            contactViewHolder.rep_send_img_d.setImageResource(R.mipmap.ic_video);
                        } else {
                            contactViewHolder.reps_send_img_txt.setText("Document");
                            contactViewHolder.r_img_rep_send.setText(rep_msg.get(0).getName());
                            contactViewHolder.rep_send_img_d.setImageResource(R.mipmap.ic_f_file);
                        }

                        contactViewHolder.timerp_send_reply.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.VISIBLE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        String path = Paths.up_load + chatList.getMessage();
                        Glide.with(context).load(path).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.send_repl_img);
                        contactViewHolder.timestamp_send_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.fd_img.setVisibility(View.GONE);

                    } else if (m_type.startsWith("video/")) {
                        String miType = rep_msg.get(0).getMessage();
                        if (miType == null) {
                            contactViewHolder.reps_send_img_txt.setText(rep_msg.get(0).getMessage());
                            contactViewHolder.r_img_rep_send.setText(rep_msg.get(0).getName());
                            contactViewHolder.rep_send_img_d.setVisibility(View.GONE);
                        } else if (miType.startsWith("image/")) {
                            contactViewHolder.reps_send_img_txt.setText("Photo");
                            contactViewHolder.rep_send_img_d.setVisibility(View.VISIBLE);
                            contactViewHolder.r_img_rep_send.setText(rep_msg.get(0).getName());
                            Glide.with(context).load(rep_msg.get(0).getPath()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.rep_send_img_d);
                        } else if (miType.startsWith("audio/")) {
                            contactViewHolder.reps_send_img_txt.setText("Voice");
                            contactViewHolder.r_img_rep_send.setText(rep_msg.get(0).getName());
                            contactViewHolder.rep_send_img_d.setImageResource(R.mipmap.voice_msg);
                        } else if (miType.startsWith("video/")) {
                            contactViewHolder.reps_send_img_txt.setText("Video");
                            contactViewHolder.r_img_rep_send.setText(rep_msg.get(0).getName());
                            contactViewHolder.rep_send_img_d.setImageResource(R.mipmap.ic_video);
                        } else {
                            contactViewHolder.reps_send_img_txt.setText("Document");
                            contactViewHolder.r_img_rep_send.setText(rep_msg.get(0).getName());
                            contactViewHolder.rep_send_img_d.setImageResource(R.mipmap.ic_f_file);
                        }

                        contactViewHolder.timerp_send_reply.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.VISIBLE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.vi_play_l.setVisibility(View.VISIBLE);
                        final String imag = Paths.up_load + chatList.getMessage();
                       // Glide.with(context).load(imag).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.send_repl_img);
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

                        mediaMetadataRetriever .setDataSource(Paths.up_load + chatList.getMessage(), new HashMap<String, String>());
                        Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(1000);
                        contactViewHolder.send_repl_img.setImageBitmap(bmFrame);
                        contactViewHolder.fd_img.setVisibility(View.GONE);
                    } else if (m_type.startsWith("audio/")) {
                        contactViewHolder.vo_sent.setVisibility(View.VISIBLE);
                        contactViewHolder.fd_voice_rt.setVisibility(View.GONE);

                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.VISIBLE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        String miType = rep_msg.get(0).getMessage();
                        if (miType == null) {
                            contactViewHolder.vo_rep_send.setText(rep_msg.get(0).getMessage());
                            contactViewHolder.vo_send_name.setText(rep_msg.get(0).getName());
                            contactViewHolder.vo_send_doc_d.setVisibility(View.GONE);
                        } else if (miType.startsWith("image/")) {
                            contactViewHolder.vo_rep_send.setText("Photo");
                            contactViewHolder.vo_send_doc_d.setVisibility(View.VISIBLE);
                            contactViewHolder.vo_send_name.setText(rep_msg.get(0).getName());
                            Glide.with(context).load(rep_msg.get(0).getPath()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.vo_send_doc_d);
                        } else if (miType.startsWith("audio/")) {
                            contactViewHolder.vo_rep_send.setText("Voice");
                            contactViewHolder.vo_send_name.setText(rep_msg.get(0).getName());
                            contactViewHolder.vo_send_doc_d.setImageResource(R.mipmap.voice_msg);
                        } else if (miType.startsWith("video/")) {
                            contactViewHolder.vo_rep_send.setText("Video");
                            contactViewHolder.vo_send_name.setText(rep_msg.get(0).getName());
                            contactViewHolder.vo_send_doc_d.setImageResource(R.mipmap.ic_video);
                        } else {
                            contactViewHolder.vo_rep_send.setText("Document");
                            contactViewHolder.vo_send_name.setText(rep_msg.get(0).getName());
                            contactViewHolder.vo_send_doc_d.setImageResource(R.mipmap.ic_f_file);
                        }
                        contactViewHolder.timestamp_vc_send.setText(getDate(Long.parseLong(chatList.getMessage_date())));

                        final String uri = Paths.up_load + chatList.getMessage();
                        contactViewHolder.ply_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {


                                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                        clearMediaPlayer();
                                        contactViewHolder.seekBar.setProgress(0);
                                        wasPlaying = true;
                                        contactViewHolder.ply_btn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
                                    }


                                    if (!wasPlaying) {

                                        if (mediaPlayer == null) {
                                            mediaPlayer = new MediaPlayer();
                                        }

                                        contactViewHolder.ply_btn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.pause_btn));

                                        //AssetFileDescriptor descriptor = context.getAssets().openFd(uri.toString());
                                        mediaPlayer.setDataSource(uri.toString());

                                        mediaPlayer.prepare();
                                        mediaPlayer.setVolume(0.5f, 0.5f);
                                        mediaPlayer.setLooping(false);
                                        contactViewHolder.seekBar.setMax(mediaPlayer.getDuration());

                                        mediaPlayer.start();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int currentPosition = mediaPlayer.getCurrentPosition();
                                                int total = mediaPlayer.getDuration();


                                                while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        currentPosition = mediaPlayer.getCurrentPosition();
                                                    } catch (InterruptedException e) {
                                                        return;
                                                    } catch (Exception e) {
                                                        return;
                                                    }

                                                    contactViewHolder.seekBar.setProgress(currentPosition);

                                                }
                                            }
                                        }).start();

                                    }

                                    wasPlaying = false;
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                                //Toast.makeText(context, "under process", Toast.LENGTH_SHORT).show();
                            }
                        });

                        contactViewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                                contactViewHolder.timer_t.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                                contactViewHolder.timer_t.setVisibility(View.VISIBLE);
                                int x = (int) Math.ceil(progress / 1000f);

                                if (x < 10)
                                    contactViewHolder.timer_t.setText("0:0" + x);
                                else
                                    contactViewHolder.timer_t.setText("0:" + x);

                                double percent = progress / (double) seekBar.getMax();
                                int offset = seekBar.getThumbOffset();
                                int seekWidth = seekBar.getWidth();
                                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                                int labelWidth = contactViewHolder.timer_t.getWidth();
                                contactViewHolder.timer_t.setX(offset + seekBar.getX() + val
                                        - Math.round(percent * offset)
                                        - Math.round(percent * labelWidth / 2));

                                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                                    clearMediaPlayer();
                                    contactViewHolder.ply_btn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
                                    seekBar.setProgress(0);
                                }

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {


                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                    mediaPlayer.seekTo(seekBar.getProgress());
                                }
                            }
                        });

                    } else {
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.VISIBLE);
                        contactViewHolder.fd_doc_rt.setVisibility(View.GONE);
                        String miType = rep_msg.get(0).getMessage();
                        if (miType == null) {
                            contactViewHolder.reps_send_img_txt_rr.setText(rep_msg.get(0).getMessage());
                            // contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                        } else if (miType.startsWith("image/")) {
                            contactViewHolder.reps_send_img_txt_rr.setText("Photo");
                            //contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                            Glide.with(context).load(rep_msg.get(0).getPath()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.rep_rec_img_d);

                        } else if (miType.startsWith("audio/")) {
                            contactViewHolder.reps_send_img_txt_rr.setText("Voice");
                            //contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                        } else if (miType.startsWith("video/")) {
                            contactViewHolder.reps_send_img_txt_rr.setText("Video");
                            // contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                        } else {
                            contactViewHolder.reps_send_img_txt_rr.setText("Document");
                            //  contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                        }
                        contactViewHolder.timestamp_doc_rec_rr.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.doc_name_rec_rr.setText(chatList.getMessage());
                    }
                }

            } else {
                // Show right side messages those are received.
                if (chatList.getReply_id().isEmpty()) {

                    String m_type = Utils.getMimeType(chatList.getMessage());
                    if (m_type == null) {
                        contactViewHolder.show_icon_r.setVisibility(View.GONE);
                        contactViewHolder.rightMsgLayout.setVisibility(View.VISIBLE);
                        contactViewHolder.tk_name.setText(chatList.getSender_name());
                        contactViewHolder.tk_name.setVisibility(View.VISIBLE);
                        if (chatList.getForward_status().equalsIgnoreCase("1")) {
                            contactViewHolder.fd_text_rc.setVisibility(LinearLayout.VISIBLE);
                        } else {
                            contactViewHolder.fd_text_rc.setVisibility(LinearLayout.GONE);
                        }
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.txtMessage_rec.setText(chatList.getMessage());
                        contactViewHolder.timestamp_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                    } else if (m_type.startsWith("image/")) {
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        contactViewHolder.vi_play_l.setVisibility(View.GONE);
                        // contactViewHolder.rightImgLayout.setVisibility(LinearLayout.VISIBLE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.VISIBLE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);

                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);

                        Glide.with(context).load(Paths.up_load + chatList.getMessage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.leftImgeMessage_rec);

                        contactViewHolder.timestamp_rec_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                    } else if (m_type.startsWith("video/")) {
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        // contactViewHolder.rightImgLayout.setVisibility(LinearLayout.VISIBLE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.VISIBLE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);

                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play_l.setVisibility(View.VISIBLE);
                       // Glide.with(context).load(Paths.up_load + chatList.getMessage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.leftImgeMessage_rec);

                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

                        mediaMetadataRetriever .setDataSource(Paths.up_load + chatList.getMessage(), new HashMap<String, String>());
                        Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(1000);
                        contactViewHolder.leftImgeMessage_rec.setImageBitmap(bmFrame);
                        contactViewHolder.timestamp_rec_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));

                    } else if (m_type.startsWith("audio/")) {
                        contactViewHolder.vo_reply.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);

                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        final String url_voice = Paths.up_load + chatList.getMessage();

                        contactViewHolder.voice_time_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.ply_btn_rec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {


                                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                        clearMediaPlayer();
                                        contactViewHolder.seekBar_rec.setProgress(0);
                                        wasPlaying = true;
                                        contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
                                    }


                                    if (!wasPlaying) {

                                        if (mediaPlayer == null) {
                                            mediaPlayer = new MediaPlayer();
                                        }

                                        contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.pause_btn));
                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        mediaPlayer.setDataSource(url_voice);

                                        mediaPlayer.prepare();
                                        mediaPlayer.setVolume(0.5f, 0.5f);
                                        mediaPlayer.setLooping(false);
                                        contactViewHolder.seekBar_rec.setMax(mediaPlayer.getDuration());

                                        mediaPlayer.start();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int currentPosition = mediaPlayer.getCurrentPosition();
                                                int total = mediaPlayer.getDuration();


                                                while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        currentPosition = mediaPlayer.getCurrentPosition();
                                                    } catch (InterruptedException e) {
                                                        return;
                                                    } catch (Exception e) {
                                                        return;
                                                    }

                                                    contactViewHolder.seekBar_rec.setProgress(currentPosition);

                                                }
                                            }
                                        }).start();

                                    }

                                    wasPlaying = false;
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                                //Toast.makeText(context, "under process", Toast.LENGTH_SHORT).show();
                            }
                        });


                        contactViewHolder.seekBar_rec.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                                contactViewHolder.timer_t_rec.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                                contactViewHolder.timer_t_rec.setVisibility(View.VISIBLE);
                                int x = (int) Math.ceil(progress / 1000f);

                                if (x < 10)
                                    contactViewHolder.timer_t_rec.setText("0:0" + x);
                                else
                                    contactViewHolder.timer_t_rec.setText("0:" + x);

                                double percent = progress / (double) seekBar.getMax();
                                int offset = seekBar.getThumbOffset();
                                int seekWidth = seekBar.getWidth();
                                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                                int labelWidth = contactViewHolder.timer_t_rec.getWidth();
                                contactViewHolder.timer_t_rec.setX(offset + seekBar.getX() + val
                                        - Math.round(percent * offset)
                                        - Math.round(percent * labelWidth / 2));

                                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                                    clearMediaPlayer();
                                    contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
                                    seekBar.setProgress(0);
                                }

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {


                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                    mediaPlayer.seekTo(seekBar.getProgress());
                                }
                            }
                        });
                    } else {
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);

                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        if (chatList.getForward_status().equalsIgnoreCase("1")) {
                            contactViewHolder.fd_doc_rt.setVisibility(View.VISIBLE);
                        } else {
                            contactViewHolder.fd_doc_rt.setVisibility(View.GONE);
                        }
                        contactViewHolder.timestamp_doc_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.doc_name_rec.setText(chatList.getMessage());
                    }
                } else {
                    ArrayList<UserRepo> rep_msg = db_tables.getRepMessageRec(chatList.getReply_id());

                    String my_type = Utils.getMimeType(chatList.getMessage());
                    if (my_type == null) {
                        contactViewHolder.reply_l2.setVisibility(View.VISIBLE);
                        contactViewHolder.tk_name.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(LinearLayout.GONE);
                        contactViewHolder.mesg_reps_reply.setText(rep_msg.get(0).getMessage());
                        contactViewHolder.txtMessage_send_rec_rep.setText(chatList.getMessage());
                        contactViewHolder.user_nnmae.setText(chatList.getSender_name());
                        contactViewHolder.timestamp_send_rp_t.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                    } else if (my_type.startsWith("audio/")) {
                        contactViewHolder.vo_reply.setVisibility(View.VISIBLE);
                        contactViewHolder.fd_voice_lt.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);

                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);

                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);

                        String miType = rep_msg.get(0).getMessage();
                        if (miType == null) {
                            contactViewHolder.voic_reply_1.setText(rep_msg.get(0).getMessage());
                            //contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                        } else if (miType.startsWith("image/")) {
                            contactViewHolder.voic_reply_1.setText("Photo");
                            contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                            Glide.with(context).load(rep_msg.get(0).getPath()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.voice_img_dp);

                        } else if (miType.startsWith("audio/")) {
                            contactViewHolder.voic_reply_1.setText("Voice");
                            contactViewHolder.voice_img_dp.setImageResource(R.mipmap.voice_msg);
                        } else if (miType.startsWith("video/")) {
                            contactViewHolder.voic_reply_1.setText("Video");
                            contactViewHolder.voice_img_dp.setImageResource(R.mipmap.ic_video);
                        } else {
                            contactViewHolder.voic_reply_1.setText("Document");
                            contactViewHolder.voice_img_dp.setImageResource(R.mipmap.ic_f_file);
                        }
                        contactViewHolder.voice_time_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));

                        final String url_voice = Paths.up_load + chatList.getMessage();

                        contactViewHolder.voice_time_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        if (chatList.getForward_status().equalsIgnoreCase("1")) {
                            contactViewHolder.fd_voice_lt.setVisibility(View.VISIBLE);
                        } else {
                            contactViewHolder.fd_voice_lt.setVisibility(View.GONE);
                        }
                        contactViewHolder.ply_btn_rec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {


                                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                        clearMediaPlayer();
                                        contactViewHolder.seekBar_rec.setProgress(0);
                                        wasPlaying = true;
                                        contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
                                    }


                                    if (!wasPlaying) {

                                        if (mediaPlayer == null) {
                                            mediaPlayer = new MediaPlayer();
                                        }

                                        contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.pause_btn));
                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        mediaPlayer.setDataSource(url_voice);

                                        mediaPlayer.prepare();
                                        mediaPlayer.setVolume(0.5f, 0.5f);
                                        mediaPlayer.setLooping(false);
                                        contactViewHolder.seekBar_rec.setMax(mediaPlayer.getDuration());

                                        mediaPlayer.start();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int currentPosition = mediaPlayer.getCurrentPosition();
                                                int total = mediaPlayer.getDuration();


                                                while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        currentPosition = mediaPlayer.getCurrentPosition();
                                                    } catch (InterruptedException e) {
                                                        return;
                                                    } catch (Exception e) {
                                                        return;
                                                    }

                                                    contactViewHolder.seekBar_rec.setProgress(currentPosition);

                                                }
                                            }
                                        }).start();

                                    }

                                    wasPlaying = false;
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                                //Toast.makeText(context, "under process", Toast.LENGTH_SHORT).show();
                            }
                        });


                        contactViewHolder.seekBar_rec.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                                contactViewHolder.timer_t_rec.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                                contactViewHolder.timer_t_rec.setVisibility(View.VISIBLE);
                                int x = (int) Math.ceil(progress / 1000f);

                                if (x < 10)
                                    contactViewHolder.timer_t_rec.setText("0:0" + x);
                                else
                                    contactViewHolder.timer_t_rec.setText("0:" + x);

                                double percent = progress / (double) seekBar.getMax();
                                int offset = seekBar.getThumbOffset();
                                int seekWidth = seekBar.getWidth();
                                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                                int labelWidth = contactViewHolder.timer_t_rec.getWidth();
                                contactViewHolder.timer_t_rec.setX(offset + seekBar.getX() + val
                                        - Math.round(percent * offset)
                                        - Math.round(percent * labelWidth / 2));

                                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                                    clearMediaPlayer();
                                    contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
                                    seekBar.setProgress(0);
                                }

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {


                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                    mediaPlayer.seekTo(seekBar.getProgress());
                                }
                            }
                        });

                    } else if (my_type.startsWith("image/") || my_type.startsWith("video/")) {
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        // contactViewHolder.rightImgLayout.setVisibility(LinearLayout.VISIBLE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.reply_l2.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.VISIBLE);
                        contactViewHolder.vi_play_l.setVisibility(View.VISIBLE);

                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        String path = Paths.up_load + chatList.getMessage();

//                        Glide.with(context).load(path).asBitmap().dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                contactViewHolder.r_img_rec_rep.setImageBitmap(resource);
//                            }
//                        });
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

                        mediaMetadataRetriever .setDataSource(Paths.up_load + chatList.getMessage(), new HashMap<String, String>());
                        Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(1000);
                        contactViewHolder.r_img_rec_rep.setImageBitmap(bmFrame);
                        //Glide.with(context).load(path).into(contactViewHolder.r_img_rec_rep);
                        String miType = rep_msg.get(0).getMessage();
                        if (miType == null) {
                            contactViewHolder.mesg_reps_reply_1.setText(rep_msg.get(0).getMessage());


                        } else if (miType.startsWith("image/")) {
                            contactViewHolder.mesg_reps_reply_1.setText("Photo");
                            Glide.with(context).load(rep_msg.get(0).getPath()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.rep_rec_img_d);

                        } else if (miType.startsWith("audio/")) {
                            contactViewHolder.mesg_reps_reply_1.setText("Voice");
                        } else if (miType.startsWith("video/")) {
                            contactViewHolder.mesg_reps_reply_1.setText("Video");
                        } else {
                            contactViewHolder.mesg_reps_reply_1.setText("Doc");
                        }
                        contactViewHolder.r_rp_t.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.fd_img_rt.setVisibility(View.GONE);
                    } else {
                        contactViewHolder.rightMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_left_ll.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.leftImgLayout.setVisibility(View.GONE);
                        contactViewHolder.rightImgLayout.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_img.setVisibility(View.GONE);
                        contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
                        contactViewHolder.reply_l2_doc.setVisibility(View.VISIBLE);
                        contactViewHolder.fd_doc_rt.setVisibility(View.GONE);
                        String miType = rep_msg.get(0).getMessage();
                        if (miType == null) {
                            contactViewHolder.reps_send_img_txt_rr.setText(rep_msg.get(0).getMessage());
                            // contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                        } else if (miType.startsWith("image/")) {
                            contactViewHolder.reps_send_img_txt_rr.setText("Photo");
                            //contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                            Glide.with(context).load(rep_msg.get(0).getPath()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.rep_rec_img_d);

                        } else if (miType.startsWith("audio/")) {
                            contactViewHolder.reps_send_img_txt_rr.setText("Voice");
                            //contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                        } else if (miType.startsWith("video/")) {
                            contactViewHolder.reps_send_img_txt_rr.setText("Video");
                            // contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                        } else {
                            contactViewHolder.reps_send_img_txt_rr.setText("Document");
                            //  contactViewHolder.r_img_rep_send_rr.setText(rep_msg.get(0).getName());
                        }
                        contactViewHolder.timestamp_doc_rec_rr.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.doc_name_rec_rr.setText(chatList.getMessage());
                    }
                }
            }
            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));
            applyClickEvents(contactViewHolder, matchesList, position, value);
            //applyIconAnimation(contactViewHolder, matchesList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_app_item_view2, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        return myHoder;
    }


    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        protected TextView txtMessage_send;
        protected TextView txtMessage_rec;
        protected TextView timestamp_send;
        protected TextView timestamp_rec;
        protected TextView timestamp_send_img;
        protected TextView timestamp_rec_img;
        protected TextView timestamp_vc_send;
        protected TextView voice_time_rec;
        protected TextView doc_name;
        protected TextView doc_name_rec;
        protected TextView timestamp_doc_sent;
        protected TextView timestamp_doc_rec;
        protected TextView mesg_reps_send;
        protected TextView user_nnmae_rep_send;
        protected TextView txtMessage_send_reply;
        protected TextView timestamp_send_reply;
        protected TextView mesg_reps_reply;
        protected TextView txtMessage_send_rec_rep;
        protected TextView timestamp_send_rp_t;
        protected TextView r_img_rep_send;
        protected TextView reps_send_img_txt;
        protected TextView timerp_send_reply;
        protected TextView r_rp_t;
        protected TextView mesg_reps_reply_1;
        protected TextView doc_send_name;
        protected TextView doc_rep_send;
        protected TextView doc_name_rep_send;
        protected TextView timestamp_doc_sent_rep;
        protected TextView r_img_rep_send_rr;
        protected TextView reps_send_img_txt_rr;
        protected TextView doc_name_rec_rr;
        protected TextView timestamp_doc_rec_rr;
        protected TextView fwText;
        protected TextView fd_text_rc;
        protected TextView fd_img;
        protected TextView fd_img_rt;
        protected TextView fd_voice_rt;
        protected TextView fd_voice_lt;
        protected TextView fd_doc_lt;
        protected TextView fd_doc_rt;
        protected TextView vo_send_name;
        protected TextView vo_rep_send;
        protected TextView voic_reply_1;
        protected TextView my_header_date;
        protected TextView user_nnmae;
        protected TextView uu_name;
        protected TextView tk_name;


        protected ImageView msg_viewd;
        protected ImageView msg_sent;
        protected ImageView msg_sent_img;
        protected ImageView txtMessage_send_img;
        protected ImageView leftImgeMessage_rec;
        protected ImageView msg_sent_voice;
        protected ImageView doc_img_rec;
        protected ImageView doc_img;
        protected ImageView doc_msg_sent;
        protected ImageView msg_sent_reply;
        protected ImageView rec_rp_type;
        protected ImageView rep_send_v;
        protected ImageView rec_img_pic;
        protected ImageView rep_send_img_d;
        protected ImageView send_repl_img;
        protected ImageView re_sent_reply;
        protected ImageView r_img_rec_rep;
        protected ImageView rep_rec_img_d;
        protected ImageView doc_img_rep;
        protected ImageView doc_msg_sent_rep;
        protected ImageView doc_img_rec_rr;
        protected ImageView vo_send_doc_d;
        protected ImageView voice_img_dp;
        protected ImageView show_icon;
        protected ImageView show_icon_r;


        protected LinearLayout leftMsgLayout;
        protected LinearLayout rightMsgLayout;
        protected LinearLayout leftImgLayout;
        protected LinearLayout rightImgLayout;
        protected LinearLayout chat_right_voice;
        protected LinearLayout chat_left_voice;
        protected LinearLayout documet_right_ll;
        protected LinearLayout documet_left_ll;
        protected LinearLayout reply_l1_doc;
        protected LinearLayout reply_l2_doc;

        public LinearLayout messageContainer;
        public LinearLayout reply_l1;
        public LinearLayout reply_l2;
        public LinearLayout reply_l1_img;
        public LinearLayout reply_l2_img;
        public LinearLayout vi_play;
        public LinearLayout vi_play_l;
        public LinearLayout vo_reply;
        public LinearLayout vo_sent;
        public LinearLayout d_header;
        public ProgressBar sender_pp;
        public ProgressBar reciver_pp;


        ImageView ply_btn;
        ImageView ply_btn_rec;
        TextView timer_t;

        TextView timer_t_rec;
        SeekBar seekBar;
        SeekBar seekBar_rec;

        public ContactViewHolder(View v) {
            super(v);
            txtMessage_send = (TextView) v.findViewById(R.id.txtMessage_send);
            txtMessage_rec = (TextView) v.findViewById(R.id.txtMessage_rec);
            timestamp_rec_img = (TextView) v.findViewById(R.id.timestamp_rec_img);
            timestamp_send = (TextView) v.findViewById(R.id.timestamp_send);
            timestamp_rec = (TextView) v.findViewById(R.id.timestamp_rec);
            timestamp_send_img = (TextView) v.findViewById(R.id.timestamp_send_img);
            timestamp_vc_send = (TextView) v.findViewById(R.id.timestamp_vc_send);
            voice_time_rec = (TextView) v.findViewById(R.id.voice_time_rec);
            doc_name = (TextView) v.findViewById(R.id.doc_name);
            doc_name_rec = (TextView) v.findViewById(R.id.doc_name_rec);
            timestamp_doc_sent = (TextView) v.findViewById(R.id.timestamp_doc_sent);
            timestamp_doc_rec = (TextView) v.findViewById(R.id.timestamp_doc_rec);
            mesg_reps_reply = (TextView) v.findViewById(R.id.mesg_reps_reply);
            txtMessage_send_rec_rep = (TextView) v.findViewById(R.id.txtMessage_send_rec_rep);
            timestamp_send_rp_t = (TextView) v.findViewById(R.id.timestamp_send_rp_t);
            r_img_rep_send = (TextView) v.findViewById(R.id.r_img_rep_send);
            reps_send_img_txt = (TextView) v.findViewById(R.id.reps_send_img_txt);
            timerp_send_reply = (TextView) v.findViewById(R.id.timerp_send_reply);
            r_rp_t = (TextView) v.findViewById(R.id.r_rp_t);
            mesg_reps_reply_1 = (TextView) v.findViewById(R.id.mesg_reps_reply_1);
            doc_send_name = (TextView) v.findViewById(R.id.doc_send_name);
            doc_rep_send = (TextView) v.findViewById(R.id.doc_rep_send);
            doc_name_rep_send = (TextView) v.findViewById(R.id.doc_name_rep_send);
            timestamp_doc_sent_rep = (TextView) v.findViewById(R.id.timestamp_doc_sent_rep);
            r_img_rep_send_rr = (TextView) v.findViewById(R.id.r_img_rep_send_rr);
            reps_send_img_txt_rr = (TextView) v.findViewById(R.id.reps_send_img_txt_rr);
            doc_name_rec_rr = (TextView) v.findViewById(R.id.doc_name_rec_rr);
            timestamp_doc_rec_rr = (TextView) v.findViewById(R.id.timestamp_doc_rec_rr);
            fwText = (TextView) v.findViewById(R.id.fd_text);
            fd_text_rc = (TextView) v.findViewById(R.id.fd_text_rc);
            fd_img = (TextView) v.findViewById(R.id.fd_img);
            fd_img_rt = (TextView) v.findViewById(R.id.fd_img_rt);
            fd_voice_rt = (TextView) v.findViewById(R.id.fd_voice_rt);
            fd_voice_lt = (TextView) v.findViewById(R.id.fd_voice_lt);
            fd_doc_lt = (TextView) v.findViewById(R.id.fd_doc_lt);
            fd_doc_rt = (TextView) v.findViewById(R.id.fd_doc_rt);
            vo_send_name = (TextView) v.findViewById(R.id.vo_send_name);
            vo_rep_send = (TextView) v.findViewById(R.id.vo_rep_send);
            voic_reply_1 = (TextView) v.findViewById(R.id.voic_reply_1);
            tk_name = (TextView) v.findViewById(R.id.tk_name);


            my_header_date = (TextView) v.findViewById(R.id.date_header);

            rec_rp_type = (ImageView) v.findViewById(R.id.rec_rp_type);
            rep_send_v = (ImageView) v.findViewById(R.id.rep_send_v);
            rec_img_pic = (ImageView) v.findViewById(R.id.rec_img_pic);
            rep_send_img_d = (ImageView) v.findViewById(R.id.rep_send_img_d);
            send_repl_img = (ImageView) v.findViewById(R.id.send_repl_img);
            re_sent_reply = (ImageView) v.findViewById(R.id.re_sent_reply);
            r_img_rec_rep = (ImageView) v.findViewById(R.id.r_img_rec_rep);
            rep_rec_img_d = (ImageView) v.findViewById(R.id.rep_rec_img_d);
            doc_img_rep = (ImageView) v.findViewById(R.id.doc_img_rep);
            doc_msg_sent_rep = (ImageView) v.findViewById(R.id.doc_msg_sent_rep);
            doc_img_rec_rr = (ImageView) v.findViewById(R.id.doc_img_rec_rr);
            vo_send_doc_d = (ImageView) v.findViewById(R.id.vo_send_doc_d);
            voice_img_dp = (ImageView) v.findViewById(R.id.voice_img_dp);
            show_icon = (ImageView) v.findViewById(R.id.show_icon);
            show_icon_r = (ImageView) v.findViewById(R.id.show_icon_r);

            mesg_reps_send = (TextView) v.findViewById(R.id.mesg_reps_send);
            user_nnmae_rep_send = (TextView) v.findViewById(R.id.user_nnmae_rep_send);
            user_nnmae = (TextView) v.findViewById(R.id.user_nnmae);
            txtMessage_send_reply = (TextView) v.findViewById(R.id.txtMessage_send_reply);
            timestamp_send_reply = (TextView) v.findViewById(R.id.timestamp_send_reply);
            uu_name = (TextView) v.findViewById(R.id.uu_name);

            msg_sent = (ImageView) v.findViewById(R.id.msg_sent);
            msg_sent_img = (ImageView) v.findViewById(R.id.msg_sent_img);
            msg_sent_voice = (ImageView) v.findViewById(R.id.msg_sent_voice);
            txtMessage_send_img = (ImageView) v.findViewById(R.id.txtMessage_send_img);
            leftImgeMessage_rec = (ImageView) v.findViewById(R.id.leftImgeMessage_rec);
            doc_img_rec = (ImageView) v.findViewById(R.id.doc_img_rec);
            doc_img = (ImageView) v.findViewById(R.id.doc_img);
            doc_msg_sent = (ImageView) v.findViewById(R.id.doc_msg_sent);
            msg_sent_reply = (ImageView) v.findViewById(R.id.msg_sent_reply);


            leftMsgLayout = itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = itemView.findViewById(R.id.chat_right_msg_layout);
            leftImgLayout = itemView.findViewById(R.id.chat_left_Image);
            rightImgLayout = itemView.findViewById(R.id.chat_right_Image);
            chat_right_voice = itemView.findViewById(R.id.chat_right_voice);
            chat_left_voice = itemView.findViewById(R.id.chat_left_voice);
            documet_left_ll = itemView.findViewById(R.id.documet_left_ll);
            documet_right_ll = itemView.findViewById(R.id.documet_right_ll);
            reply_l1 = itemView.findViewById(R.id.reply_l1);
            reply_l2 = itemView.findViewById(R.id.reply_l2);
            reply_l1_img = itemView.findViewById(R.id.reply_l1_img);
            reply_l2_img = itemView.findViewById(R.id.reply_l2_img);
            reply_l1_doc = itemView.findViewById(R.id.reply_l1_doc);
            reply_l2_doc = itemView.findViewById(R.id.reply_l2_doc);
            d_header = v.findViewById(R.id.d_header);

            messageContainer = (LinearLayout) v.findViewById(R.id.messageContainer);
            vi_play = (LinearLayout) v.findViewById(R.id.vi_play);
            vi_play_l = (LinearLayout) v.findViewById(R.id.vi_play_l);
            vo_reply = (LinearLayout) v.findViewById(R.id.vo_reply);
            vo_sent = (LinearLayout) v.findViewById(R.id.vo_sent);

            ply_btn = v.findViewById(R.id.ply_btn);
            ply_btn_rec = v.findViewById(R.id.ply_btn_rec);
            timer_t = v.findViewById(R.id.timer_t);
            timer_t_rec = v.findViewById(R.id.timer_t_rec);
            seekBar = v.findViewById(R.id.seekbar);
            seekBar_rec = v.findViewById(R.id.seekbar_rec);
            reciver_pp = v.findViewById(R.id.img_process_r);
            sender_pp = v.findViewById(R.id.img_process_s);
            v.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(matchesList, getAdapterPosition(), false);
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public interface ChatAdapterListener {
        void onMessageRowClicked(int position, String contactName, String contact_name, String pic_url, String contact_phone);

        void onMessageShow(List<MessageObject> matchesList, int position);

        void onMessageDocs(List<MessageObject> matchesList, int position);

        void onMessageImages(List<MessageObject> matchesList, int position);

        void onMessageView(List<MessageObject> matchesList, int position);

        void onRowLongClicked(List<MessageObject> matchesList, int position, boolean value);
    }

    private String getDate(long timeStamp) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "date";
        }
    }

    private void applyClickEvents(final ContactViewHolder holder, final List<MessageObject> matchesList, final int position, final boolean value) {

        holder.reply_l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageView(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.leftImgeMessage_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageShow(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.r_img_rec_rep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageShow(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.documet_left_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageShow(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.reply_l2_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageShow(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.reply_l1_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageImages(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.documet_right_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageDocs(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.reply_l2_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageShow(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.reply_l1_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageDocs(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.rightMsgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageView(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.leftMsgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onMessageView(matchesList, position);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.leftImgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageView(matchesList, position);
            }
        }); holder.rightImgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageView(matchesList, position);
            }
        });
        holder.chat_right_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.rightMsgLayout.setEnabled(true);
        holder.rightMsgLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(matchesList, position, value);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
        holder.leftMsgLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(matchesList, position, value);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });

        holder.leftImgLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(matchesList, position, value);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
        holder.documet_left_ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(matchesList, position, value);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
        holder.documet_right_ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(matchesList, position, value);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
        holder.reply_l1_doc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(matchesList, position, value);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });

    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
