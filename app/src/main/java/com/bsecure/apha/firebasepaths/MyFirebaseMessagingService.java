package com.bsecure.apha.firebasepaths;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.database.DB_Tables;
import com.bsecure.apha.utils.SharedValues;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Admin on 2018-09-27.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    String m_type, msg_id, msg_date, sender_member_id, district_id, sender_member_number, receiver_member_number, sender_name, rep_Id;
    String status_code,messsages;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
//                JSONObject json= new JSONObject(remoteMessage.getData().toString());
//                myvales=String.valueOf(json);
                Map<String, String> params = remoteMessage.getData();
                Object object = new JSONObject(params);
                // setBadge(getApplicationContext(), badge);
                sendPushNotification(object);
            } catch (Exception e) {
                //sendPushNotification(object);
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void sendPushNotification(Object json) {
        DB_Tables db_tables = new DB_Tables(this);
        try {
            //"image": "",
            //	"message": "{\"msg\":\"Hello! Welcome To APP\",\"msg_det\":\"SM,14*1549099152*12*3336*1*4*vasudeva rao\"}"
            JSONObject data = new JSONObject(json.toString());
            String data_silent = data.optString("silent");
            if (data_silent.equalsIgnoreCase("true")) {
                String message_data = data.getString("message");
                JSONObject object = new JSONObject(message_data.toString());
                String messsages = object.optString("msg");
                String title_msg = object.optString("msg_det");
                String arry_data[] = title_msg.split("\\*");
                m_type = arry_data[0];
                status_code = arry_data[1];
                if (m_type.equalsIgnoreCase("MST")) {
                    if (status_code.equalsIgnoreCase("0")) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getredAlert("Your Account Has Been Inactive - Contact Administator");
                            }
                        });
                        sendBD();
                    }
                } else if (m_type.equalsIgnoreCase("MAS")) {
                    if (status_code.equalsIgnoreCase("1")) {
                        SharedValues.saveValue(this, "approval_status", status_code);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getredAlert("Your Account Has Been Approved");
                            }
                        });


                    } else {

                        SharedValues.saveValue(this, "approval_status", status_code);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getredAlert("Your Account not Approved");
                            }
                        });


                    }
                } else if (m_type.equalsIgnoreCase("MPS")) {
                    if (status_code.equalsIgnoreCase("1")) {
                        SharedValues.saveValue(this, "paid_status", status_code);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getredAlert("Your Account Has Been Renewal & Proceed to do all actions on application");
                            }
                        });

                    } else {
                        SharedValues.saveValue(this, "paid_status", status_code);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getredAlert("Your Account Has Been Expired & prevent to do all actions on application");
                            }
                        });

                    }
                } else if (m_type.equalsIgnoreCase("MSS")) {
                    if (status_code.equalsIgnoreCase("1")) {
                        SharedValues.saveValue(this, "subscription_status", status_code);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getredAlert("Your Account Has Been Subscribed & Proceed to do all actions on application");
                            }
                        });

                    } else {
                        SharedValues.saveValue(this, "subscription_status", status_code);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        getredAlert("Your Account Has Been Unsubscribed & prevent to do all actions on application");
                                    }
                                });
                            }
                        });

                    }
                } else if (m_type.equalsIgnoreCase("MDU")) {
                    if (status_code.equalsIgnoreCase("1")) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        getredAlert("Your Designation Has Been Changed - Please Contact Admin For Any Queries");
                                    }
                                });
                            }
                        });
                        sendBD();
                    }
                } else if (m_type.equalsIgnoreCase("MBU")) {
                    if (status_code.equalsIgnoreCase("1")) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        getredAlert("Your Mobile Number Has Been Changed - Please Contact Admin For Any Queries");
                                    }
                                });
                            }
                        });

                        sendBD();
                    }
                }
                //    MU*member_name*business_name*email*member_address1*member_address2*state_id*state*district_id*district
                else if (m_type.equalsIgnoreCase("MU")) {

                    String member_name = arry_data[1];
                    String business_name = arry_data[2];
                    String state_id = arry_data[6];
                    String state = arry_data[7];
                    String district_id = arry_data[8];
                    String district = arry_data[9];
                    String member_id = arry_data[10];
                    String user_pic = arry_data[11];
                    SharedValues.saveValue(this, "business_name", business_name);
                    SharedValues.saveValue(this, "sender_name", member_name);
                    SharedValues.saveValue(this, "state_id", state_id);
                    SharedValues.saveValue(this, "district_id", district_id);
                    SharedValues.saveValue(this, "district", district);
                    SharedValues.saveValue(this, "user_pic", user_pic);
                    SharedValues.saveValue(this, "state", state);
                    SharedValues.saveValue(this, "member_id", member_id);
                    db_tables.update_member(member_name, business_name, state_id, state, district_id, district, member_id, user_pic);

                    Intent test=new Intent();
                    test.setAction("com.profile.action");
                    sendBroadcast(test);
                }

            } else {

                String imageUrl = data.optString("image");
                String message_data = data.getString("message");
                JSONObject object = new JSONObject(message_data);
                messsages = object.optString("msg");
                String title_msg = object.optString("msg_det");
                String arry_data[] = title_msg.split("\\*");
                String receiver_member_number1 = SharedValues.getValue(this, "member_number");
                m_type = arry_data[0];
                if (m_type.equalsIgnoreCase("SM")) {
                    msg_id = arry_data[1];
                    msg_date = arry_data[2];
                    sender_member_id = arry_data[3];
                    district_id = arry_data[4];
                    sender_member_number = arry_data[5];
                    receiver_member_number = arry_data[6];
                    sender_name = arry_data[7];
                   // messsages = arry_data[8];

                    if (receiver_member_number.equals("4") && !receiver_member_number1.equals("4")) {
                        db_tables.messageData(messsages, msg_id, msg_date, sender_member_id, district_id, receiver_member_number, receiver_member_number1, sender_name, "1");
                        db_tables.updateMemberList(msg_date, messsages,receiver_member_number,district_id,"1");
                    } else {
                        db_tables.messageData(messsages, msg_id, msg_date, sender_member_id, district_id, sender_member_number, receiver_member_number, sender_name, "1");
                        db_tables.updateMemberList(msg_date, messsages,receiver_member_number,district_id,"1");
                    }
                } else if (m_type.equalsIgnoreCase("FM")) {
                    msg_id = arry_data[1];
                    msg_date = arry_data[2];
                    sender_member_id = arry_data[3];
                    district_id = arry_data[4];
                    sender_member_number = arry_data[5];
                    receiver_member_number = arry_data[6];
                    sender_name = arry_data[7];
                   // messsages = arry_data[8];

                    if (receiver_member_number.equals("4") && !receiver_member_number1.equals("4")) {
                        db_tables.messageData(messsages, msg_id, msg_date, sender_member_id, district_id, receiver_member_number, receiver_member_number1, sender_name, "1");
                        db_tables.updateForword(msg_date);
                        db_tables.updateMemberList(msg_date, messsages,receiver_member_number,district_id,"1");
                    } else {
                        db_tables.messageData(messsages, msg_id, msg_date, sender_member_id, district_id, sender_member_number, receiver_member_number, sender_name, "1");
                        db_tables.updateForword(msg_date);
                        db_tables.updateMemberList(msg_date, messsages,receiver_member_number,district_id,"1");
                    }
                } else if (m_type.equalsIgnoreCase("RM")) {
                    msg_id = arry_data[1];
                    msg_date = arry_data[2];
                    sender_member_id = arry_data[3];
                    district_id = arry_data[4];
                    sender_member_number = arry_data[5];
                    receiver_member_number = arry_data[6];
                    sender_name = arry_data[7];
                    rep_Id = arry_data[8];
                    if (receiver_member_number.equals("4") && !receiver_member_number1.equals("4")) {
                        db_tables.messageData(messsages, msg_id, msg_date, sender_member_id, district_id, receiver_member_number, receiver_member_number1, sender_name, "1");
                        db_tables.UpdateRpId(msg_date, rep_Id);
                        db_tables.updateMemberList(msg_date, messsages,receiver_member_number,district_id,"1");
                    } else {
                        db_tables.messageData(messsages, msg_id, msg_date, sender_member_id, district_id, sender_member_number, receiver_member_number, sender_name, "1");
                        db_tables.UpdateRpId(msg_date, rep_Id);
                        db_tables.updateMemberList(msg_date, messsages,receiver_member_number,district_id,"1");
                    }
                } else if (m_type.equalsIgnoreCase("RTAM")) {
                    msg_id = arry_data[1];
                    msg_date = arry_data[2];
                    sender_member_id = arry_data[3];
                    district_id = arry_data[4];
                    sender_member_number = arry_data[5];
                    receiver_member_number = arry_data[6];
                    sender_name = arry_data[7];
                    rep_Id = arry_data[8];
                    if (receiver_member_number.equals("4") && !receiver_member_number1.equals("4")) {
                        db_tables.messageData(messsages, msg_id, msg_date, sender_member_id, district_id, receiver_member_number, receiver_member_number1, sender_name, "1");
                        db_tables.UpdateRpId(msg_date, rep_Id);
                        db_tables.updateMemberList(msg_date, messsages,receiver_member_number,district_id,"1");
                    } else {
                        db_tables.messageData(messsages, msg_id, msg_date, sender_member_id, district_id, sender_member_number, receiver_member_number, sender_name, "1");
                        db_tables.UpdateRpId(msg_date, rep_Id);
                        db_tables.updateMemberList(msg_date, messsages,receiver_member_number,district_id,"1");
                    }
                } else if (m_type.equalsIgnoreCase("SUM")) {
                    msg_id = arry_data[1];
                    msg_date = arry_data[2];
                    sender_member_id = arry_data[3];
                    district_id = arry_data[4];
                    sender_member_number = arry_data[5];
                    receiver_member_number = arry_data[6];
                    sender_name = arry_data[7];
                    if (receiver_member_number.equals("4") && !receiver_member_number1.equals("4")) {
                        db_tables.messageData(messsages, msg_id, msg_date, sender_member_id, district_id, receiver_member_number, receiver_member_number1, sender_name, "0");
                        db_tables.updateForword(msg_date);
                        db_tables.updateMemberList(msg_date, messsages,receiver_member_number,district_id,"1");
                    } else {
                        db_tables.messageData(messsages, msg_id, msg_date, sender_member_id, district_id, sender_member_number, receiver_member_number, sender_name, "0");
                        db_tables.updateForword(msg_date);
                        db_tables.updateMemberList(msg_date, messsages,receiver_member_number,district_id,"1");
                    }
                }

                MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
                Intent intent = new Intent(this, AccociateMain.class);

                //if there is no image
                if (imageUrl.equals("null")||imageUrl.isEmpty()) {
                    mNotificationManager.showSmallNotification(sender_name, messsages, intent);
                    myBroadcaster(message_data);
                } else {
                    mNotificationManager.showBigNotification(sender_name, "", imageUrl, intent);
                    myBroadcaster(message_data);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void myBroadcaster(String message_data) {
        Intent intent = new Intent();
        intent.setAction("com.acc.app.BROADCAST_NOTIFICATION");
        intent.putExtra("NotificationData", "1");
        intent.putExtra("receiver_member_number", receiver_member_number);
        sendBroadcast(intent);
    }

    private void sendBD() {
        Intent intent = new Intent();
        intent.setAction("com.acc.app.SESSION");
        sendBroadcast(intent);
    }

    private void getredAlert(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
