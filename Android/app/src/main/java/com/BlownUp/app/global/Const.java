package com.BlownUp.app.global;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.BlownUp.app.MainApplication;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Const {

    //ENV
    public final static boolean DEV_MODE = false;
    public final static boolean PUBLISH_MODE = true;
    public final static boolean LIVE_PAYMENT = true;

    //ACCOUNT SOCIAL TYPE
    public final static int NORMAL_ACCOUNT = 0;
    public final static int GOOGLE_ACCOUNT = 1;

    //STRIPE PUBLISHABLE KEY
    public final static String STRIPE_PK_TEST = "pk_test_51IVQTuFmwQHroLNotyVUdfmRP83uYbtaecmidNUa1JdtnLUpySuEx5mzhF1E4fm46VG038uvsLBWBkaDYV72WZfV00vRbnMLv0";
    public final static String STRIPE_PK_LIVE = "pk_live_51IVQTuFmwQHroLNo5y9JhLuPnnbpMC2aG0PKGiNqAiuVjN5B4SCzURwetu4ZFNZzix6SV5XLTfp4O3THStK7OyGo002pHFXAxT";

    public final static String STRIPE_PK = LIVE_PAYMENT ? STRIPE_PK_LIVE : STRIPE_PK_TEST;

    public final static String GOOGLE_MERCHANT_ID = "BCR2DN6TV6J55HR7";

    //TERMS AND CONDITIONS
    public final static String APP_LANDING_URL = "https://blownup.co";
    public final static String TERMS_CONDITIONS_URL = "https://blownup.co/terms-and-conditions";
    public final static String PRIVACY_POLICY_URL = "https://blownup.co/privacy-policy";

    //FRAGMENTS UNIQUE TAGS
    public final static String HEADER_FRAGMENT = "com.BlownUp.app.screen.fragments.HEADER_FRAGMENT";
    public final static String SCHEDULE_LIST_FRAGMENT = "com.BlownUp.app.screen.fragments.SCHEDULE_LIST_FRAGMENT";
    public final static String SCHEDULE_ADD_FRAGMENT = "com.BlownUp.app.screen.fragments.SCHEDULE_ADD_FRAGMENT";
    public final static String CONTACT_LIST_FRAGMENT = "com.BlownUp.app.screen.fragments.CONTACT_LIST_FRAGMENT";
    public final static String HELP_FRAGMENT = "com.BlownUp.app.screen.fragments.HELP_FRAGMENT";
    public final static String ACCOUNT_FRAGMENT = "com.BlownUp.app.screen.fragments.ACCOUNT_FRAGMENT";
    public final static String CONTACT_ADD_FRAGMENT = "com.BlownUp.app.screen.fragments.CONTACT_ADD_FRAGMENT";

    //STORE ID
    public final static String MY_PREFS_NAME = "com.BlownUp.app";
    public final static int MY_PREFS_MODE = Context.MODE_PRIVATE;

    //STORE KEY
    public final static String REMEMBER_ME = "REMEMBER_ME";
    public final static String USER_PROFILE = "USER_PROFILE";
    public final static String CHARGED = "CHARGED";
    public final static String RINGTONE_INCOMING = "RINGTONE_INCOMING";

    //NOTIFICATION ID
    public static final int INCOMING_CALL_ID = 32456;

    //NOTIFICATION CHANNEL
    public static final String INCOMING_CALL_CHANNEL_NAME = "INCOMING_CALL_CHANNEL_NAME";
    public static final String INCOMING_CALL_CHANNEL_DESCRIPTION = "INCOMING_CALL_CHANNEL_DESCRIPTION";

    //LOG TYPE
    public final static int LOG_ERROR = 0;
    public final static int LOG_LOG = 1;

    //VERSION CHECK
    public final static boolean checkVersionCode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    //SD CARD DIR
    public final static String APP_SDCARD_DIR_PATH = android.os.Environment.getExternalStorageDirectory().toString() + "/BlownUp";

    //API ENDPOINTS URLS
    public final static String DEV_SERVER = "http://10.0.2.2";
    public final static String PRODUCTION_SERVER = PUBLISH_MODE ? "https://panel.blownup.co" : "http://dev-panel.blownup.co";
    public final static String BASE_URL = DEV_MODE ? DEV_SERVER : PRODUCTION_SERVER;
    //SIGN IN/SIGN UP
    public final static String LOGIN_URL = BASE_URL + "/api/login";
    public final static String SIGN_UP_URL = BASE_URL + "/api/signup";
    public final static String FORGET_PASSWORD_URL = BASE_URL + "/api/password/forget";
    public final static String RESET_PASSWORD_URL = BASE_URL + "/api/password/reset";
    //ACCOUNT
    public final static String ACCOUNT_DEVICE_TOKEN_UPDATE_URL = BASE_URL + "/api/account/update_device_token";
    public final static String ACCOUNT_UPDATE_URL = BASE_URL + "/api/account/update";
    //CHECKOUT
    public final static String CHECKOUT_CHARGE_URL = BASE_URL + "/api/charge";
    public final static String CHECKOUT_STATUS_URL = BASE_URL + "/api/charge/status";
    //CONTACT
    public final static String CONTACT_GET_URL = BASE_URL + "/api/contact";
    public final static String CONTACT_ADD_URL = BASE_URL + "/api/contact/add";
    public final static String CONTACT_UPDATE_URL = BASE_URL + "/api/contact/update";
    public final static String CONTACT_DELETE_URL = BASE_URL + "/api/contact/delete";
    //SCHEDULE CALL
    public final static String SCHEDULE_GET_URL = BASE_URL + "/api/schedule";
    public final static String SCHEDULE_ADD_URL = BASE_URL + "/api/schedule/add";
    public final static String SCHEDULE_UPDATE_URL = BASE_URL + "/api/schedule/update";
    public final static String SCHEDULE_DELETE_URL = BASE_URL + "/api/schedule/delete";
    //HELP
    public final static String HELP_GET_URL = BASE_URL + "/api/help";

    public static void clearIncomingCallNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(INCOMING_CALL_ID);
        MainApplication.stopRingTone();
    }
}
