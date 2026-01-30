package com.example.todolist.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolist.R;

public class SettingsFragment extends Fragment {

    private MainViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupNotificationCheckbox(view, R.id.check_notify_9, 9, 0);
        setupNotificationCheckbox(view, R.id.check_notify_12, 12, 0);
        setupNotificationCheckbox(view, R.id.check_notify_18, 18, 0);
    }

    private void setupNotificationCheckbox(View view, int resId, int hour, int minute) {
        CheckBox checkBox = view.findViewById(resId);
        checkBox.setChecked(mViewModel.isNotificationEnabled(hour));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mViewModel.scheduleNotification(hour, minute, isChecked);
        });
    }
}
