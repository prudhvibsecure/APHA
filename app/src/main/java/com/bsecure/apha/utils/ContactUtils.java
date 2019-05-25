package com.bsecure.apha.utils;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by Admin on 2018-12-04.
 */

public class ContactUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private static boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    public static String getTimeAgo(long time) {
//        if (time < 1000000000000L) {
//            // if timestamp given in seconds, convert to millis
//            time *= 1000;
//        }
//
//        long now = System.currentTimeMillis();
//        if (time > now || time <= 0) {
//            return null;
//        }
//
//        // TODO: localize
//        final long diff = now - time;
//        if (diff < MINUTE_MILLIS) {
//            return "just now";
//        } else if (diff < 2 * MINUTE_MILLIS) {
//            return "a minute ago";
//        } else if (diff < 50 * MINUTE_MILLIS) {
//            return diff / MINUTE_MILLIS + " minutes ago";
//        } else if (diff < 90 * MINUTE_MILLIS) {
//            return "an hour ago";
//        } else if (diff < 24 * HOUR_MILLIS) {
//            return diff / HOUR_MILLIS + " hours ago";
//        } else if (diff < 48 * HOUR_MILLIS) {
//            return "yesterday";
//        } else {
//            return diff / DAY_MILLIS + " days ago";

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return DateFormat.format(timeFormatString, smsTime).toString();
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format("dd/MM/yyyy", smsTime).toString();
        } else {
            return DateFormat.format("dd/MM/yyyy", smsTime).toString();
        }

    }

    public static String getTimeAgolatest(long time) {

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Today";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format("dd MMMM yyyy", smsTime).toString();
        } else {
            return DateFormat.format("dd MMMM yyyy", smsTime).toString();
        }

    }

}
