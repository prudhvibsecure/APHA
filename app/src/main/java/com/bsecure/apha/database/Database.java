package com.bsecure.apha.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private final static String APP_DATABASE_NAME = "Associate_new.db";
    private final static int APP_DATABASE_VERSION = 1;


    public final String CREATE_MESSAGES = "CREATE TABLE messages(message_id TEXT, message TEXT, message_date TEXT, sender_member_id TEXT, receiver_member_ids TEXT, district_id TEXT, sender_member_number TEXT, receiver_member_number TEXT, reply_id TEXT, sender_name TEXT, forward_ids TEXT, forward_status TEXT,user_me TEXT);";
    public final String CREATE_MEMBERS = "CREATE TABLE members(member_id TEXT primary key, member_name TEXT, reg_mobile_no TEXT, profile_image TEXT, email TEXT, business_name TEXT, member_address1 TEXT, member_address2 TEXT, designation TEXT, state_id TEXT, state TEXT, district_id TEXT, district TEXT, added_date TEXT, status TEXT, expiry_date TEXT, approval_status TEXT, paid_status TEXT, subscription_status TEXT ,member_number TEXT);";
    public final String CREATE_MEMBERS_v = "CREATE TABLE members_list(id TEXT primary key, name TEXT,member_number TEXT,last_message TEXT,time TEXT,flag TEXT);";

    public Database(Context context) {
        super(context, APP_DATABASE_NAME, null, APP_DATABASE_VERSION);
    }

    public Database(Context context, String APP_DATABASE_NAME,
                    int APP_DATABASE_VERSION) {
        super(context, APP_DATABASE_NAME, null, APP_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MESSAGES);
        db.execSQL(CREATE_MEMBERS);
        db.execSQL(CREATE_MEMBERS_v);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
//        if (newVersion > oldVersion) {
//            //db.execSQL(CREATE_TABLE_MSGS_SENT_1);
//        }
//        if (newVersion == oldVersion) {
//            onCreate(db);
//        }

    }


}
