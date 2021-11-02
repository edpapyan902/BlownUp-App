package com.BlownUp.app.screen.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.BlownUp.app.R;

public class SettingFragment extends BaseFragment {

    private static SettingFragment instance = null;
    private View mView;

    public static SettingFragment getInstance() {
        if (instance == null) {
            instance = new SettingFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting, container, false);

        return mView;
    }
}