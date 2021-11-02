package com.BlownUp.app.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.BlownUp.app.MainApplication;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.models.Schedule;
import com.BlownUp.app.network.API;
import com.BlownUp.app.store.Store;
import com.BlownUp.app.utils.Utils;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class DeviceBootReceiver extends BroadcastReceiver {

    //action intents
    private final String INTENT_ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private final String INTENT_ACTION_REBOOT = "android.intent.action.REBOOT";
    private final String INTENT_ACTION_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON";
    private final String INTENT_ACTION_LOCKED_BOOT_COMPLETED = "android.intent.action.LOCKED_BOOT_COMPLETED";
    //alarm
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(INTENT_ACTION_BOOT_COMPLETED)
                || action.equals(INTENT_ACTION_REBOOT)
                || action.equals(INTENT_ACTION_QUICKBOOT_POWERON)
                || action.equals(INTENT_ACTION_LOCKED_BOOT_COMPLETED)) {

            String token = MainApplication.getUser(context).token;
            if (!TextUtils.isEmpty(token)) {
                API.GET(token, Const.SCHEDULE_GET_URL, new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                JSONObject json_data = response.getJSONObject("data");
                                JSONArray json_schedules = json_data.getJSONArray("schedules");

                                if (json_schedules.length() > 0) {
                                    for (int i = 0; i < json_schedules.length(); i++) {
                                        Schedule schedule = (Schedule) Utils.JSON_STR2OBJECT(json_schedules.get(i).toString(), Schedule.class);
                                        setAlarmSchedule(context, schedule);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
            }
        }
    }

    public void setAlarmSchedule(Context context, Schedule schedule) {
        Store.setString(context, schedule.alarm_identify, Utils.OBJECT2JSON_STR(schedule, Schedule.class));

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.SCHEDULE_ALARM_IDENTIFY, schedule.alarm_identify);
        alarmIntent = PendingIntent.getBroadcast(context, Utils.getRandomCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String[] result = schedule.scheduled_at.split(" ");
        String[] result_date = result[0].split("-");
        String[] result_time = result[1].split(":");
        int year = Integer.valueOf(result_date[0]);
        int month = Integer.valueOf(result_date[1]) - 1;
        int day = Integer.valueOf(result_date[2]);
        int hour = Integer.valueOf(result_time[0]);
        int minute = Integer.valueOf(result_time[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }
}