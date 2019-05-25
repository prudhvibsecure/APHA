package com.bsecure.apha.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bsecure.apha.models.UserRepo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 2018-12-05.
 */

public class DB_Tables {

    private Context context;

    private Database database = null;
    SQLiteDatabase db;

    public DB_Tables(Context context) {
        database = new Database(context);
    }

    public DB_Tables openDB() {
        try {
            db = database.getWritableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    public void close() {
        try {
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToMembers(String member_id, String member_name, String approval_status, String member_number, String paid_status, String subscription_status, String business_name, String reg_mobile_no
            , String state_id, String state, String district_id, String district, String added_date, String expiry_date, String img) {

        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("member_id", member_id);
                cv.put("member_name", member_name);
                cv.put("approval_status", approval_status);
                cv.put("member_number", member_number);
                cv.put("paid_status", paid_status);
                cv.put("subscription_status", subscription_status);
                cv.put("business_name", business_name);
                cv.put("reg_mobile_no", reg_mobile_no);
                cv.put("state_id", state_id);
                cv.put("state", state);
                cv.put("district_id", district_id);
                cv.put("district", district);
                cv.put("expiry_date", expiry_date);
                cv.put("added_date", added_date);
                cv.put("profile_image", img);
                db.insertWithOnConflict("members", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update_member(String member_name, String business_name, String state_id, String state, String district_id, String district, String member_id, String img) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "member_id='" + member_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("member_name", member_name);
                cv.put("business_name", business_name);
                cv.put("state_id", state_id);
                cv.put("state", state);
                cv.put("district_id", district_id);
                cv.put("district", district);
                cv.put("member_id", member_id);
                cv.put("profile_image", img);
                db.update("members", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void UserMessage(String message_date, String msg_id) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();

                String iwhereClause = "message_date='" + message_date + "'";
                ContentValues cv = new ContentValues();
                cv.put("message_id", msg_id);
                db.update("messages", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getMemberNumber(String reg_mobile_no) {
        String member_no = null;
        try {
            if (database != null) {

                String cursor_q = "select * from members where reg_mobile_no='" + reg_mobile_no + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            member_no = String.valueOf(cursor.getInt(cursor.getColumnIndex("member_number")));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return member_no;
    }

    public void messageData(String messsages, String msg_id, String msg_date, String sender_member_id, String district_id, String sender_member_number, String receiver_member_number, String sender_name, String me) {
        SQLiteDatabase db = null;
//        message_id TEXT, message TEXT, message_date TEXT, sender_member_id TEXT,
//                receiver_member_ids TEXT, district_id TEXT, sender_member_number TEXT,
//                receiver_member_number TEXT, reply_id TEXT, sender_name TEXT, forward_ids TEXT, forward_status TEXT
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("message_id", msg_id);
                cv.put("message", messsages);
                cv.put("message_date", msg_date);
                cv.put("sender_member_id", sender_member_id);
                cv.put("receiver_member_ids", "");
                cv.put("district_id", district_id);
                cv.put("sender_member_number", sender_member_number);
                cv.put("receiver_member_number", receiver_member_number);
                cv.put("reply_id", "");
                cv.put("sender_name", sender_name);
                cv.put("forward_ids", district_id);
                cv.put("forward_status", "0");
                cv.put("user_me", me);
                db.insert("messages", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateRpId(String mesg_date_time, String reply_Id) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();

                String iwhereClause = "message_date='" + mesg_date_time + "'";
                ContentValues cv = new ContentValues();
                cv.put("reply_id", reply_Id);
                db.update("messages", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateForword(String mesg_date_time) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();

                String iwhereClause = "message_date='" + mesg_date_time + "'";
                ContentValues cv = new ContentValues();
                cv.put("forward_status", "1");
                db.update("messages", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getchatList(String state_ps_id, String dist_id) {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from messages where receiver_member_number='" + state_ps_id + "' and district_id='" + dist_id + "'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("message_id", cursor.getString(cursor.getColumnIndex("message_id")));
                        json.put("message", cursor.getString(cursor.getColumnIndex("message")));
                        json.put("message_date", cursor.getString(cursor.getColumnIndex("message_date")));
                        json.put("sender_member_id", cursor.getString(cursor.getColumnIndex("sender_member_id")));
                        json.put("receiver_member_ids", cursor.getString(cursor.getColumnIndex("receiver_member_ids")));
                        json.put("district_id", cursor.getString(cursor.getColumnIndex("district_id")));
                        json.put("forward_ids", cursor.getString(cursor.getColumnIndex("forward_ids")));
                        json.put("sender_member_number", cursor.getString(cursor.getColumnIndex("sender_member_number")));
                        json.put("receiver_member_number", cursor.getString(cursor.getColumnIndex("receiver_member_number")));
                        json.put("sender_name", cursor.getString(cursor.getColumnIndex("sender_name")));
                        json.put("reply_id", cursor.getString(cursor.getColumnIndex("reply_id")));
                        json.put("forward_status", cursor.getString(cursor.getColumnIndex("forward_status")));
                        json.put("user_me", cursor.getString(cursor.getColumnIndex("user_me")));
                        array.put(json);
                    }

                    jsonObject.put("message_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getchatList_view(String presedent_id, String receiver_member_number, String distrct_id) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                //String sql = "select * from messages where (district_id='"+distrct_id+"' and (sender_member_number='" + presedent_id + "' and receiver_member_number='" + receiver_member_number + "') or (sender_member_number='" + receiver_member_number + "' and receiver_member_number='" + presedent_id + "'))";
                String sql = "select * from messages where district_id='" + distrct_id + "'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("message_id", cursor.getString(cursor.getColumnIndex("message_id")));
                        json.put("message", cursor.getString(cursor.getColumnIndex("message")));
                        json.put("message_date", cursor.getString(cursor.getColumnIndex("message_date")));
                        json.put("sender_member_id", cursor.getString(cursor.getColumnIndex("sender_member_id")));
                        json.put("receiver_member_ids", cursor.getString(cursor.getColumnIndex("receiver_member_ids")));
                        json.put("district_id", cursor.getString(cursor.getColumnIndex("district_id")));
                        json.put("forward_ids", cursor.getString(cursor.getColumnIndex("forward_ids")));
                        json.put("sender_member_number", cursor.getString(cursor.getColumnIndex("sender_member_number")));
                        json.put("receiver_member_number", cursor.getString(cursor.getColumnIndex("receiver_member_number")));
                        json.put("sender_name", cursor.getString(cursor.getColumnIndex("sender_name")));
                        json.put("reply_id", cursor.getString(cursor.getColumnIndex("reply_id")));
                        json.put("forward_status", cursor.getString(cursor.getColumnIndex("forward_status")));
                        json.put("user_me", cursor.getString(cursor.getColumnIndex("user_me")));
                        array.put(json);
                    }

                    jsonObject.put("message_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public ArrayList<UserRepo> getRepMessage(String reply_id) {
        ArrayList<UserRepo> mArrayList = new ArrayList<UserRepo>();
        String message = null;
        String sender_name = null;
        String type = null;
        try {
            if (database != null) {

                String cursor_q = "select message,sender_name from messages where message_id='" + reply_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor && !reply_id.equalsIgnoreCase("0") || reply_id.isEmpty())
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            UserRepo repo = new UserRepo();
                            message = cursor.getString(cursor.getColumnIndex("message"));
                            sender_name = cursor.getString(cursor.getColumnIndex("sender_name"));
                            repo.setMessage(message);
                            repo.setName(sender_name);
                            mArrayList.add(repo);
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArrayList;
    }

    public ArrayList<UserRepo> getRepMessageRec(String reply_id) {
        ArrayList<UserRepo> mArrayList = new ArrayList<UserRepo>();
        String message = null;
        String sender_name = null;
        String type = null;
        try {
            if (database != null) {

                String cursor_q = "select message,sender_name from messages where message_id='" + reply_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {

                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        UserRepo repo = new UserRepo();
                        message = cursor.getString(cursor.getColumnIndex("message"));
                        sender_name = cursor.getString(cursor.getColumnIndex("sender_name"));
                        repo.setMessage(message);
                        repo.setName(sender_name);
                        mArrayList.add(repo);
                    }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArrayList;
    }


    public String getLastMessage(String presedent_id, String receiver_member_number) {

        String last_message = null;
        try {
            if (database != null) {

                String cursor_q = "select * from messages where (sender_member_number='" + presedent_id + "' and receiver_member_number='" + receiver_member_number + "') or (sender_member_number='" + receiver_member_number + "' and receiver_member_number='" + presedent_id + "')";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToLast();
                            last_message = cursor.getString(cursor.getColumnIndex("message"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return last_message;
    }

    public String getLastMessageDate(String presedent_id, String receiver_member_number) {

        String last_message = null;
        try {
            if (database != null) {

                String cursor_q = "select * from messages where (sender_member_number='" + presedent_id + "' and receiver_member_number='" + receiver_member_number + "') or (sender_member_number='" + receiver_member_number + "' and receiver_member_number='" + presedent_id + "')";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToLast();
                            last_message = cursor.getString(cursor.getColumnIndex("message_date"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return last_message;
    }

    public void addMemberList(String id, String name, String member_number) {
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("id", id);
                cv.put("name", name);
                cv.put("member_number", member_number);
                cv.put("last_message", "");
                cv.put("time", "");
                cv.put("flag", "0");
                db.insertWithOnConflict("members_list", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMemberList(String mesg_date_time, String message, String pId, String id, String flag) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();

                String iwhereClause = "id='" + id + "'";
                ContentValues cv = new ContentValues();
                cv.put("last_message", message);
                cv.put("time", mesg_date_time);
                cv.put("flag", flag);
                db.update("members_list", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Object getmember_number(String district_id) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();
                String sql = "select * from members_list";
                //SELECT * FROM todos td, tags tg, todo_tags tt WHERE tg.tag_name = ‘Watchlist’ AND tg.id = tt.tag_id AND td.id = tt.todo_id;
                // String sql = "select * from members_list ml,messages msg where ml.id='"+district_id+"' and msg.district_id='"+district_id+"'";
                // String sql = " select id,name,member_number,time,message FROM members as mb,messages as msg WHERE mb.id= msg.district_id AND MAX(msg.id)";
                // String sql=" SELECT * FROM members_list a INNER JOIN messages b ON a.id=b.district_id WHERE b.district_id='"+district_id+"'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("id", cursor.getString(cursor.getColumnIndex("id")));
                        json.put("name", cursor.getString(cursor.getColumnIndex("name")));
                        json.put("member_number", cursor.getString(cursor.getColumnIndex("member_number")));
                        json.put("time", cursor.getString(cursor.getColumnIndex("time")));
                        json.put("last_message", cursor.getString(cursor.getColumnIndex("last_message")));
                        json.put("flag", cursor.getString(cursor.getColumnIndex("flag")));
                        array.put(json);
                    }

                    jsonObject.put("member_details", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void updateCount(String id) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();

                String iwhereClause = "id='" + id + "'";
                ContentValues cv = new ContentValues();
                cv.put("flag", "0");
                db.update("members_list", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
