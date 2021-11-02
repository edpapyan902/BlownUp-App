package com.BlownUp.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.BlownUp.app.global.Const;
import com.BlownUp.app.models.User;
import com.BlownUp.app.store.Store;
import com.BlownUp.app.utils.Utils;
import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.stripe.android.PaymentConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MainApplication extends Application {

    private static String fcmToken = "";
    private static Activity mCurrentActivity = null;

    private static Ringtone ringtone = null;

    public static User getUser(Context context) {
        User user = new User();
        String user_data = Store.getString(context, Const.USER_PROFILE);
        try {
            JSONObject user_json = new JSONObject(user_data);
            user = (User) Utils.JSON_STR2OBJECT(user_json.toString(), User.class);
        } catch (JSONException e) {
            Log.d("json_parse_error----->", user_data);
        }
        return user;
    }

    public static String getFCMToken() {
        return fcmToken;
    }

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public static void setCurrentActivity(Activity activity) {
        mCurrentActivity = activity;
    }

    public static void playRingTone(Context context) {
        String incoming_ringtone = Store.getString(context, Const.RINGTONE_INCOMING);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (!TextUtils.isEmpty(incoming_ringtone))
            uri = Uri.parse("content://media" + incoming_ringtone);

        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }

        ringtone = RingtoneManager.getRingtone(context, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ringtone.setLooping(true);
        }
        ringtone.play();
    }

    public static void stopRingTone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Stripe Configuration
        PaymentConfiguration.init(getApplicationContext(), Const.STRIPE_PK);

        //Android Networking Initialize
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        //Thread Policy for Http Request
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //VM Ignore Policy for File Download
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //Crash Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        //Get FCM Token
        initFCM();
    }

    public void initFCM() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    fcmToken = task.getResult();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
        Thread.UncaughtExceptionHandler defaultUEH;

        public ExceptionHandler() {
            defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        }

        @SuppressWarnings("deprecation")
        public void uncaughtException(Thread thread, Throwable exception) {

            StringWriter stackTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(stackTrace));
            Utils.appendLog(stackTrace.toString(), Const.LOG_ERROR);

            defaultUEH.uncaughtException(thread, exception);
            System.exit(0);
        }
    }
}
