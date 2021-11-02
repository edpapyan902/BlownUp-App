package com.BlownUp.app.screen.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.thekhaeng.pushdownanim.PushDownAnim;

public class HeaderFragment extends BaseFragment implements View.OnClickListener {

    private View mView;
    private ImageButton btnAdd;

    private static HeaderFragment instance = null;

    public static HeaderFragment getInstance() {
        if (instance == null) {
            instance = new HeaderFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_header, container, false);

        initLayout();
        return mView;
    }

    private void initLayout() {
        btnAdd = mView.findViewById(R.id.btnAdd);

        PushDownAnim.setPushDownAnimTo(btnAdd)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                add();
                break;
        }
    }

    private void add() {
        mainInstance.setCurrentSchedule(null);
        mainInstance.startMainFragment(ScheduleAddFragment.getInstance(), Const.SCHEDULE_ADD_FRAGMENT);
    }
}