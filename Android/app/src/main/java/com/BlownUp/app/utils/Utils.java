package com.BlownUp.app.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.BlownUp.app.global.Const;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.BlownUp.app.global.Const.APP_SDCARD_DIR_PATH;

public class Utils {

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidUSPhone(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber))
            return false;

        String regExp = "\\(?\\d{3}\\)?[ -]?\\d{3}[ -]?\\d{4}";
        return phoneNumber.matches(regExp);
    }

    public static String formatPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber))
            return "";

        phoneNumber = phoneNumber.replace(" ", "");
        phoneNumber = phoneNumber.replace(")", "");
        phoneNumber = phoneNumber.replace("(", "");
        phoneNumber = phoneNumber.replace("-", "");

        String formattedNumber = "";    // (123) 456-7890
        formattedNumber += "(";
        formattedNumber += phoneNumber.substring(0, 3);
        formattedNumber += ") ";
        formattedNumber += phoneNumber.substring(3, 6);
        formattedNumber += "-";
        formattedNumber += phoneNumber.substring(6, 10);

        return formattedNumber;
    }

    public static Object JSON_STR2OBJECT(String strJson, Class tClass) {
        Gson gson = new Gson();
        Object object = gson.fromJson(strJson, tClass);
        return object;
    }

    public static String OBJECT2JSON_STR(Object object, Class tClass) {
        Gson gson = new Gson();
        String strJson = gson.toJson(object);
        return strJson;
    }

    public static String Date2StrTime(String date) {
        String[] resultDate = date.split(" ");
        String[] resultTime = resultDate[1].split(":");
        int hour = Integer.valueOf(resultTime[0]);
        int minute = Integer.valueOf(resultTime[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat sdfDate = new SimpleDateFormat("hh:mm a");

        return sdfDate.format(calendar.getTime());
    }

    public static String Date2StrDate(String date) {
        String[] resultDate = date.split(" ");
        String[] resultTime = resultDate[0].split("-");
        int year = Integer.valueOf(resultTime[0]);
        int month = Integer.valueOf(resultTime[1]) - 1;
        int day = Integer.valueOf(resultTime[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");

        return sdfDate.format(calendar.getTime());
    }

    public static void appendLog(String log, int type) {
        if (Const.PUBLISH_MODE) {
            return;
        }

        final String LINE_SEPARATOR = "\n";

        StringBuilder errorReport = new StringBuilder();
        errorReport.append(log);
        errorReport.append(LINE_SEPARATOR);

        File dir = new File(APP_SDCARD_DIR_PATH + "/Logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "New_LOG.txt");
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            buf.append(currentDateTimeString + ":" + errorReport.toString());
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeMarqueeText(TextView textView) {
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSelected(true);
        textView.setSingleLine(true);
    }

    public static String UNIX_TIMESTAMP2DATE(long timestamp, String format) {
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);
        return sdfDate.format(date);
    }

    public static String TIMESTAMP2DATE(long timestamp, String format) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);
        return sdfDate.format(date);
    }

    public static long Time2Millisecond(String date) {
        String[] resultDate = date.split(" ");
        String[] resultTime = resultDate[0].split("-");
        int year = Integer.valueOf(resultTime[0]);
        int month = Integer.valueOf(resultTime[1]) - 1;
        int day = Integer.valueOf(resultTime[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTimeInMillis();
    }

    public static long Date2Millisecond(String date) {
        String[] result = date.split(" ");
        String[] resultDate = result[0].split("-");
        String[] resultTime = result[1].split(":");
        int year = Integer.valueOf(resultDate[0]);
        int month = Integer.valueOf(resultDate[1]) - 1;
        int day = Integer.valueOf(resultDate[2]);
        int hour = Integer.valueOf(resultTime[0]);
        int minute = Integer.valueOf(resultTime[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        return calendar.getTimeInMillis();
    }

    public static String PLUS0(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }

    public static String DATE2STR(Date date, String format) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);
        return sdfDate.format(date);
    }

    public static int getRandomCode() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(900000);
    }

    public static boolean isRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (context.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

    public static String getCurrentActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return componentInfo.getClassName();
    }
}