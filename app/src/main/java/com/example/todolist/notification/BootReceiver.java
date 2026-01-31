package com.example.todolist.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences("ToDoPrefs", Context.MODE_PRIVATE);
            String json = prefs.getString("notification_times", "[]");
            
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int hour = obj.getInt("hour");
                    int minute = obj.getInt("minute");
                    boolean enabled = obj.getBoolean("enabled");
                    
                    if (enabled) {
                        int requestCode = obj.has("requestCode") ? obj.getInt("requestCode") : (hour * 60 + minute);
                        AlarmScheduler.scheduleAlarm(context, hour, minute, requestCode);
                        Log.d("BootReceiver", "Rescheduled alarm for " + hour + ":" + minute);
                    }
                }
            } catch (Exception e) {
                Log.e("BootReceiver", "Error parsing notifications json", e);
            }
        }
    }
}
