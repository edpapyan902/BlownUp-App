package com.BlownUp.app.call;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static com.BlownUp.app.global.Const.clearIncomingCallNotification;

public class IncomingCallActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ACTION_CONTENT = "com.BlownUp.app.call.IncomingCallActivity.ACTION_CONTENT";

    public final static String CALLER_NAME = "com.BlownUp.app.call.IncomingCallActivity.CALLER_NAME";
    public final static String CALLER_NUMBER = "com.BlownUp.app.call.IncomingCallActivity.CALLER_NUMBER";
    public final static String CALLER_AVATAR = "com.BlownUp.app.call.IncomingCallActivity.CALLER_AVATAR";

    private ImageView imgAvatar, imgAcceptCall, imgDeclineCall, imgEndCall;
    private TextView txtName, txtNumber;

    private static IncomingCallActivity instance = null;

    public static IncomingCallActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_incoming_call);

        instance = this;

        initLayout();
        setInitData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        MainApplication.setCurrentActivity(this);
    }

    private void initLayout() {
        imgAvatar = findViewById(R.id.imgAvatar);
        imgAcceptCall = findViewById(R.id.imgAcceptCall);
        imgDeclineCall = findViewById(R.id.imgDeclineCall);
        imgEndCall = findViewById(R.id.imgEndCall);
        imgAcceptCall.setOnClickListener(this);
        imgDeclineCall.setOnClickListener(this);
        imgEndCall.setOnClickListener(this);

        txtName = findViewById(R.id.txtName);
        txtNumber = findViewById(R.id.txtNumber);

        Utils.makeMarqueeText(txtName);
        Utils.makeMarqueeText(txtNumber);
    }

    private void setInitData() {
        Intent intent = getIntent();
        String name = intent.getStringExtra(CALLER_NAME);
        String number = intent.getStringExtra(CALLER_NUMBER);
        String avatar = intent.getStringExtra(CALLER_AVATAR);

        drawItem(name, number, avatar);
    }

    public void drawItem(String name, String number, String avatar) {
        if (!TextUtils.isEmpty(name)) {
            txtName.setText(name);
            txtNumber.setText(number);
        } else {
            txtName.setText(number);
            txtNumber.setVisibility(View.GONE);
        }

        Picasso.get().load(Const.BASE_URL + avatar).into(imgAvatar, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                imgAvatar.setImageResource(R.drawable.ic_default_avatar);
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainApplication.stopRingTone();

        if (MainApplication.getCurrentActivity() == this) {
            MainApplication.setCurrentActivity(null);
        }

        instance = null;

        clearIncomingCallNotification(this);

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgAcceptCall:
            case R.id.imgDeclineCall:
            case R.id.imgEndCall:
                finish();
                break;
        }
    }
}