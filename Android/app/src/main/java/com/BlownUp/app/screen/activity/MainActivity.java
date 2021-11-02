package com.BlownUp.app.screen.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.BlownUp.app.BaseActivity;
import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.models.Contact;
import com.BlownUp.app.models.Schedule;
import com.BlownUp.app.network.API;
import com.BlownUp.app.receiver.AlarmReceiver;
import com.BlownUp.app.screen.fragments.AccountFragment;
import com.BlownUp.app.screen.fragments.ContactListFragment;
import com.BlownUp.app.screen.fragments.HeaderFragment;
import com.BlownUp.app.screen.fragments.HelpFragment;
import com.BlownUp.app.screen.fragments.ScheduleAddFragment;
import com.BlownUp.app.screen.fragments.ScheduleListFragment;
import com.BlownUp.app.store.Store;
import com.BlownUp.app.utils.Utils;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    public final static String INIT_FRAGMENT_TYPE = "com.BlownUp.app.MainActivity.INIT_FRAGMENT_TYPE";

    private static MainActivity instance = null;
    private FragmentManager fragmentManager;
    private String currentFragmentTag = null;
    private BottomNavigationView bottomNavigationView;

    private Contact currentContact = null;
    private Schedule currentSchedule = null;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public static MainActivity getInstance() {
        if (instance == null)
            instance = new MainActivity();
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        initLayout();

        int init_frag_type = getIntent().getIntExtra(INIT_FRAGMENT_TYPE, -1);
        initFragment(init_frag_type);

        updateFCMToken();

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    private void updateFCMToken() {
        JSONObject params = new JSONObject();
        try {
            params.put("platform", "android");
            params.put("device_token", MainApplication.getFCMToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String token = MainApplication.getUser(this).token;
        API.POST(token, Const.ACCOUNT_DEVICE_TOKEN_UPDATE_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (currentFragmentTag.equals(Const.SCHEDULE_LIST_FRAGMENT)) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Are you sure?");
            alertDialog.setMessage("Do you want to exit BlownUp?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        }
    }

    public void initLayout() {
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);
    }

    private void initFragment(int type) {
        fragmentManager = getSupportFragmentManager();

        startFragment(R.id.headerContent, HeaderFragment.getInstance(), Const.HEADER_FRAGMENT);

        if (type == -1 || type == 1)
            startMainFragment(ScheduleListFragment.getInstance(), Const.SCHEDULE_LIST_FRAGMENT);
        else if (type == 0)
            startMainFragment(ScheduleAddFragment.getInstance(), Const.SCHEDULE_ADD_FRAGMENT);
        else if (type == 2)
            startMainFragment(AccountFragment.getInstance(), Const.ACCOUNT_FRAGMENT);
    }

    public void startMainFragment(Fragment fragment, String tag) {
        startFragment(R.id.mainContent, fragment, tag);
    }

    public void startFragment(int container, Fragment fragment, String tag) {
        if (!tag.equals(currentFragmentTag)) {
            fragmentManager.beginTransaction()
                    .replace(container, fragment, tag)
                    .addToBackStack(null)
                    .commit();

            currentFragmentTag = tag;
            switch (tag) {
                case Const.SCHEDULE_LIST_FRAGMENT:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_schedule_list);
                    break;
                case Const.CONTACT_LIST_FRAGMENT:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_contact_list);
                    break;
                case Const.HELP_FRAGMENT:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_help);
                    break;
                case Const.ACCOUNT_FRAGMENT:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_account);
                    break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_schedule_list:
                startMainFragment(ScheduleListFragment.getInstance(), Const.SCHEDULE_LIST_FRAGMENT);
                return true;
            case R.id.navigation_contact_list:
                startMainFragment(ContactListFragment.getInstance(), Const.CONTACT_LIST_FRAGMENT);
                return true;
            case R.id.navigation_help:
                startMainFragment(HelpFragment.getInstance(), Const.HELP_FRAGMENT);
                return true;
            case R.id.navigation_account:
                startMainFragment(AccountFragment.getInstance(), Const.ACCOUNT_FRAGMENT);
                return true;
        }
        return false;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_schedule_list:
                startMainFragment(ScheduleListFragment.getInstance(), Const.SCHEDULE_LIST_FRAGMENT);
                break;
            case R.id.navigation_contact_list:
                startMainFragment(ContactListFragment.getInstance(), Const.CONTACT_LIST_FRAGMENT);
                break;
            case R.id.navigation_help:
                startMainFragment(HelpFragment.getInstance(), Const.HELP_FRAGMENT);
                break;
            case R.id.navigation_account:
                startMainFragment(AccountFragment.getInstance(), Const.ACCOUNT_FRAGMENT);
                break;
        }
    }

    public void setCurrentContact(Contact contact) {
        currentContact = contact;
    }

    public Contact getCurrentContact() {
        return currentContact;
    }

    public void setCurrentSchedule(Schedule schedule) {
        currentSchedule = schedule;
    }

    public Schedule getCurrentSchedule() {
        return currentSchedule;
    }

    public void setAlarmSchedule(Schedule schedule) {
        Store.setString(this, schedule.alarm_identify, Utils.OBJECT2JSON_STR(schedule, Schedule.class));

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.SCHEDULE_ALARM_IDENTIFY, schedule.alarm_identify);
        alarmIntent = PendingIntent.getBroadcast(this, Utils.getRandomCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

    public void updateAlarmSchedule(Schedule schedule, String old_alarm_identify) {
        setAlarmSchedule(schedule);

        Schedule oldSchedule = new Schedule();
        oldSchedule.alarm_identify = old_alarm_identify;
        cancelAlarmSchedule(oldSchedule);
    }

    public void cancelAlarmSchedule(Schedule schedule) {
        Store.setString(this, schedule.alarm_identify, "");

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.SCHEDULE_ALARM_IDENTIFY, schedule.alarm_identify);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmIntent);
    }
}