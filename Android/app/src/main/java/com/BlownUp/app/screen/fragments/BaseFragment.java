package com.BlownUp.app.screen.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.BlownUp.app.screen.activity.MainActivity;

public class BaseFragment extends Fragment {

    public MainActivity mainInstance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainInstance = MainActivity.getInstance();
    }

    public void showToast(String message) {
        mainInstance.showToast(message);
    }

    public void showToast(int resId) {
        mainInstance.showToast(resId);
    }
}
