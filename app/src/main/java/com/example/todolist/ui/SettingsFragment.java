package com.example.todolist.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.NotificationTime;

import java.util.Calendar;

public class SettingsFragment extends Fragment implements NotificationTimesAdapter.OnTimeInteractionListener {

    private MainViewModel mViewModel;
    private NotificationTimesAdapter mAdapter;

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

        RecyclerView recyclerView = view.findViewById(R.id.recycler_notification_times);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        mAdapter = new NotificationTimesAdapter(this);
        recyclerView.setAdapter(mAdapter);

        // Observe and update list
        mViewModel.getNotificationTimes().observe(getViewLifecycleOwner(), list -> {
            mAdapter.submitList(new java.util.ArrayList<>(list)); // Submit copy to ensure diff callback runs
        });

        view.findViewById(R.id.button_add_time).setOnClickListener(v -> showTimePicker());
    }

    private void showTimePicker() {
        Calendar temp = Calendar.getInstance();
        int hour = temp.get(Calendar.HOUR_OF_DAY);
        int minute = temp.get(Calendar.MINUTE);

        TimePickerDialog picker = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute1) -> mViewModel.addNotificationTime(hourOfDay, minute1),
                hour, minute, true);
        picker.show();
    }

    @Override
    public void onToggle(NotificationTime time, boolean isEnabled) {
        mViewModel.toggleNotificationTime(time, isEnabled);
    }

    @Override
    public void onDelete(NotificationTime time) {
        mViewModel.deleteNotificationTime(time);
    }
}
