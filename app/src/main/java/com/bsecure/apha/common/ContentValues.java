package com.bsecure.apha.common;

import android.os.Environment;

/**
 * Created by Admin on 2018-11-26.
 */

public class ContentValues {

    public static String PATH = Environment.getExternalStorageDirectory()
            .toString();
    public static final String VOICE_FOLDERs = "/ASC/ASC VoiceNote";
    public static final String Images = PATH + "/ASC/ASC SentImages";
    public static final String REC_IMAGES = PATH + "/ASC/ASC ReceivedImages";
    public static final String upload_file = "https://sendsafe.com.au/Developerv1/compose/";
    public static final String filedownload = "https://sendsafe.com.au/Developerv1/assets/upload/avatar/";
    public static final String paths = "http://majicmail.com/Majic/assets/images/frontend/access_denied.png";
    public static String gr_path = "http://majicmail.com/Majic/assets/images/frontend/group-pic.png";
    public static String error_path = "http://majicmail.com/Majic/assets/images/frontend/user-unknown.jpg";
    public static String deny = "http://majicmail.com/Majic/assets/images/frontend/message_protected.png";

}
