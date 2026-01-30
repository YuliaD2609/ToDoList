package com.example.todolist.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todolist.data.AppDatabase;
import com.example.todolist.data.Category;
import com.example.todolist.data.NotificationTime;
import com.example.todolist.data.Task;
import com.example.todolist.data.TaskRepository;
import com.example.todolist.notification.AlarmScheduler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private TaskRepository mRepository;
    private LiveData<List<Task>> mAllTasks;
    private LiveData<List<Category>> mAllCategories;
    
    // Combined list for UI
    private MediatorLiveData<List<TasksAdapter.Item>> mCombinedItems = new MediatorLiveData<>();

    // Custom Notifications
    private MutableLiveData<List<NotificationTime>> mNotificationTimes = new MutableLiveData<>();
    private static final String PREFS_NAME = "ToDoPrefs";
    private static final String KEY_NOTIFICATIONS = "custom_notifications";

    public MainViewModel(Application application) {
        super(application);
        mRepository = new TaskRepository(application);
        mAllTasks = mRepository.getAllTasks();
        mAllCategories = mRepository.getAllCategories();

        // Check for old tasks cleanup on init
        mRepository.deleteOldCompletedTasks();
        
        // Merge Logic
        mCombinedItems.addSource(mAllCategories, categories -> combineData(categories, mAllTasks.getValue()));
        mCombinedItems.addSource(mAllTasks, tasks -> combineData(mAllCategories.getValue(), tasks));

        // Load Notifications
        loadNotificationTimes();
    }

    private void combineData(List<Category> categories, List<Task> tasks) {
        if (categories == null || tasks == null) {
            return; 
        }

        List<TasksAdapter.Item> items = new ArrayList<>();
        
        for (Category cat : categories) {
            items.add(new TasksAdapter.CategoryHeaderItem(cat));
            for (Task t : tasks) {
                if (t.categoryId == cat.id) {
                    items.add(new TasksAdapter.TaskItem(t));
                }
            }
        }
        mCombinedItems.setValue(items);
    }

    // --- Task & Category Methods ---

    public LiveData<List<TasksAdapter.Item>> getCombinedItems() { return mCombinedItems; }
    public LiveData<List<Task>> getAllTasks() { return mAllTasks; }
    public LiveData<List<Category>> getAllCategories() { return mAllCategories; }

    public void insert(Task task) { mRepository.insert(task); }
    public void delete(Task task) { mRepository.delete(task); }
    public void update(Task task) { mRepository.update(task); }

    public void insertCategory(Category category) { mRepository.insertCategory(category); }
    
    public void deleteCategory(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> mRepository.deleteCategory(category));
    }

    public void renameCategory(Category category, String newName) {
         category.name = newName;
         mRepository.updateCategory(category);
    }

    // --- Custom Notifications Methods ---

    public LiveData<List<NotificationTime>> getNotificationTimes() {
        return mNotificationTimes;
    }

    public void addNotificationTime(int hour, int minute) {
        List<NotificationTime> current = mNotificationTimes.getValue();
        if (current == null) current = new ArrayList<>();
        
        // Prevent duplicates
        for (NotificationTime t : current) {
            if (t.hour == hour && t.minute == minute) return;
        }

        NotificationTime newTime = new NotificationTime(hour, minute, true);
        current.add(newTime);
        sortTimes(current);
        
        mNotificationTimes.setValue(current);
        saveNotificationTimes(current);
        
        // Schedule immediately
        scheduleNotification(newTime);
    }

    public void toggleNotificationTime(NotificationTime time, boolean isEnabled) {
        List<NotificationTime> current = mNotificationTimes.getValue();
        if (current == null) return;
        
        // Find reference in list and update
        for (NotificationTime t : current) {
            if (t.equals(time)) {
                t.isEnabled = isEnabled;
                break;
            }
        }
        
        mNotificationTimes.setValue(current);
        saveNotificationTimes(current);
        
        time.isEnabled = isEnabled; // Ensure local obj matches
        scheduleNotification(time);
    }

    public void deleteNotificationTime(NotificationTime time) {
        List<NotificationTime> current = mNotificationTimes.getValue();
        if (current == null) return;
        
        current.remove(time); // Relies on equals()
        
        mNotificationTimes.setValue(current);
        saveNotificationTimes(current);
        
        // Cancel alarm
        time.isEnabled = false;
        scheduleNotification(time);
    }

    private void scheduleNotification(NotificationTime time) {
        Context context = getApplication().getApplicationContext();
        int requestCode = time.getRequestCode();
        if (time.isEnabled) {
            AlarmScheduler.scheduleAlarm(context, time.hour, time.minute, requestCode);
        } else {
            AlarmScheduler.cancelAlarm(context, requestCode);
        }
    }

    private void sortTimes(List<NotificationTime> times) {
        Collections.sort(times, (t1, t2) -> {
            int time1 = t1.hour * 60 + t1.minute;
            int time2 = t2.hour * 60 + t2.minute;
            return Integer.compare(time1, time2);
        });
    }

    private void loadNotificationTimes() {
        SharedPreferences prefs = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_NOTIFICATIONS, null);
        List<NotificationTime> loaded = new ArrayList<>();

        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    loaded.add(new NotificationTime(
                            obj.getInt("hour"),
                            obj.getInt("minute"),
                            obj.getBoolean("isEnabled")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Default initial times (as per original request: 9, 12, 18)
            loaded.add(new NotificationTime(9, 0, false));
            loaded.add(new NotificationTime(12, 0, false));
            loaded.add(new NotificationTime(18, 0, false));
        }
        sortTimes(loaded);
        mNotificationTimes.setValue(loaded);
    }

    private void saveNotificationTimes(List<NotificationTime> times) {
        SharedPreferences prefs = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        for (NotificationTime t : times) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("hour", t.hour);
                obj.put("minute", t.minute);
                obj.put("isEnabled", t.isEnabled);
                array.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        prefs.edit().putString(KEY_NOTIFICATIONS, array.toString()).apply();
    }
}
