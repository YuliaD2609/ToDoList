package com.example.todolist.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences("ToDoPrefs", Context.MODE_PRIVATE);

            checkAndSchedule(context, prefs, 9);
            checkAndSchedule(context, prefs, 12);
            checkAndSchedule(context, prefs, 18);
        }
    }

    private void checkAndSchedule(Context context, SharedPreferences prefs, int hour) {
        if (prefs.getBoolean("notify_" + hour, false)) {
            AlarmScheduler.scheduleAlarm(context, hour, 0, hour * 100);
        }
    }
}
