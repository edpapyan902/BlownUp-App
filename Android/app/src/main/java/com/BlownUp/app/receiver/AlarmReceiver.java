package com.BlownUp.app.receiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.call.IncomingCallActivity;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.models.Schedule;
import com.BlownUp.app.network.API;
import com.BlownUp.app.store.Store;
import com.BlownUp.app.utils.Utils;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.BlownUp.app.global.Const.INCOMING_CALL_CHANNEL_DESCRIPTION;
import static com.BlownUp.app.global.Const.INCOMING_CALL_CHANNEL_NAME;
import static com.BlownUp.app.global.Const.INCOMING_CALL_ID;

public class AlarmReceiver extends BroadcastReceiver {

    public final static String SCHEDULE_ALARM_IDENTIFY = "com.BlownUp.app.receiver.SCHEDULE_ALARM_IDENTIFY";

    private Context mContext;

    private String mAlarmIdentify = "";

    private AlarmManager alarmManager;

    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        mAlarmIdentify = intent.getStringExtra(SCHEDULE_ALARM_IDENTIFY);
        String json_schedule = Store.getString(context, mAlarmIdentify);
        if (TextUtils.isEmpty(json_schedule))
            return;

        Schedule schedule = (Schedule) Utils.JSON_STR2OBJECT(json_schedule, Schedule.class);
        schedule.number = schedule.contact == null ? schedule.number : schedule.contact.number;

        showInComingCallNotificationOrDirectCall(schedule.number, schedule.contact != null ? schedule.contact.avatar : "", schedule.contact != null ? schedule.contact.name : "");
        addRecentMadeCall(schedule);
        cancelAlarmSchedule(schedule);
    }

    private void addRecentMadeCall(Schedule schedule) {
        JSONObject params = new JSONObject();
        try {
            params.put("id", schedule.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Store.setString(mContext, mAlarmIdentify, "");

        String token = MainApplication.getUser(mContext).token;
        API.POST(token, Const.SCHEDULE_DELETE_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
            }
        });
    }

    public void showInComingCallNotificationOrDirectCall(String number, String avatar, String name) {
        MainApplication.playRingTone(mContext);

        Activity activity = MainApplication.getCurrentActivity();
        if (activity != null) {
            if (IncomingCallActivity.getInstance() != null) {
                IncomingCallActivity.getInstance().drawItem(name, number, avatar);
            } else {
                Intent intent = new Intent(mContext, IncomingCallActivity.class);
                intent.putExtra(IncomingCallActivity.CALLER_NAME, name);
                intent.putExtra(IncomingCallActivity.CALLER_NUMBER, number);
                intent.putExtra(IncomingCallActivity.CALLER_AVATAR, avatar);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
            }
        } else {
            Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_default_avatar);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.default_notification_channel_id))
                    .setAutoCancel(true)
                    .setDefaults(0)
                    .setUsesChronometer(false)

                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                    .setVibrate(new long[]{0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000})

                    .setFullScreenIntent(getPendingIntent(IncomingCallActivity.ACTION_CONTENT, number, avatar, name), true)

                    .setOnlyAlertOnce(true)
                    .setOngoing(true)

                    .setSmallIcon(R.drawable.ic_call_notification)
                    .setLargeIcon(largeIcon)

                    .setContentTitle(number)
                    .setContentText("Incoming Call")

                    .addAction(0, "Decline", getPendingIntent(MyBroadcastReceiver.ACTION_DECLINE, number, avatar, name))
                    .addAction(0, "Answer", getPendingIntent(MyBroadcastReceiver.ACTION_ANSWER, number, avatar, name));

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            createIncomingCallChannel(notificationManager);
            notificationManager.notify(INCOMING_CALL_ID, builder.build());
        }
    }

    @SuppressLint("WrongConstant")
    private PendingIntent getPendingIntent(String action, String number, String avatar, String name) {
        switch (action) {
            case IncomingCallActivity.ACTION_CONTENT:
                Intent intent = new Intent(mContext, IncomingCallActivity.class);
                intent.putExtra(IncomingCallActivity.CALLER_NAME, name);
                intent.putExtra(IncomingCallActivity.CALLER_NUMBER, number);
                intent.putExtra(IncomingCallActivity.CALLER_AVATAR, avatar);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK +
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                return PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            case MyBroadcastReceiver.ACTION_ANSWER:
            case MyBroadcastReceiver.ACTION_DECLINE:
                Intent intent1 = new Intent(mContext, MyBroadcastReceiver.class);
                intent1.setAction(action);
                return PendingIntent.getBroadcast(mContext, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            default:
                return null;
        }
    }

    private void createIncomingCallChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(mContext.getString(R.string.default_notification_channel_id), INCOMING_CALL_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(INCOMING_CALL_CHANNEL_DESCRIPTION);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void cancelAlarmSchedule(Schedule schedule) {
        Store.setString(mContext, schedule.alarm_identify, "");

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.SCHEDULE_ALARM_IDENTIFY, schedule.alarm_identify);
        alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmIntent);
    }
}