package com.example.todolist.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager; // Requires dependency or use Context.getSharedPreferences

import com.example.todolist.data.Category;
import com.example.todolist.data.Task;
import com.example.todolist.data.TaskRepository;
import com.example.todolist.notification.AlarmScheduler;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private TaskRepository mRepository;
    private LiveData<List<Task>> mAllTasks;
    private LiveData<List<Category>> mAllCategories;

    public MainViewModel(Application application) {
        super(application);
        mRepository = new TaskRepository(application);
        mAllTasks = mRepository.getAllTasks();
        mAllCategories = mRepository.getAllCategories();

        // Check for old tasks cleanup on init
        mRepository.deleteOldCompletedTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    public void insert(Task task) {
        mRepository.insert(task);
    }

    public void delete(Task task) {
        mRepository.delete(task);
    }

    public void update(Task task) {
        mRepository.update(task);
    }

    public void insertCategory(Category category) {
        mRepository.insertCategory(category);
    }

    public void scheduleNotification(int hour, int minute, boolean enable) {
        Context context = getApplication().getApplicationContext();
        int requestCode = hour * 100 + minute; // unique id
        if (enable) {
            AlarmScheduler.scheduleAlarm(context, hour, minute, requestCode);
        } else {
            AlarmScheduler.cancelAlarm(context, requestCode);
        }

        // Save preference
        SharedPreferences prefs = context.getSharedPreferences("ToDoPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("notify_" + hour, enable).apply();
    }

    public boolean isNotificationEnabled(int hour) {
        SharedPreferences prefs = getApplication().getApplicationContext().getSharedPreferences("ToDoPrefs",
                Context.MODE_PRIVATE);
        return prefs.getBoolean("notify_" + hour, false);
    }
}
