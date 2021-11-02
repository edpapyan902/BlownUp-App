package com.BlownUp.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.BlownUp.app.MainApplication;

import static com.BlownUp.app.global.Const.clearIncomingCallNotification;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_ANSWER = "com.BlownUp.app.receiver.MyBroadcastReceiver.ACTION_ANSWER";
    public static final String ACTION_DECLINE = "com.BlownUp.app.receiver.MyBroadcastReceiver.ACTION_DECLINE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case ACTION_ANSWER:
            case ACTION_DECLINE:
                clearIncomingCallNotification(context);
                MainApplication.stopRingTone();
                break;
        }
    }
}