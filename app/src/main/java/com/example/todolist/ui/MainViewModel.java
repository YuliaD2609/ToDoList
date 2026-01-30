package com.example.todolist.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.todolist.data.AppDatabase;
import com.example.todolist.data.Category;
import com.example.todolist.data.Task;
import com.example.todolist.data.TaskRepository;
import com.example.todolist.notification.AlarmScheduler;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private TaskRepository mRepository;
    private LiveData<List<Task>> mAllTasks;
    private LiveData<List<Category>> mAllCategories;
    
    // Combined list for UI
    private MediatorLiveData<List<TasksAdapter.Item>> mCombinedItems = new MediatorLiveData<>();

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
    }

    private void combineData(List<Category> categories, List<Task> tasks) {
        if (categories == null || tasks == null) {
            return; 
        }

        List<TasksAdapter.Item> items = new ArrayList<>();
        
        for (Category cat : categories) {
            // Add Header
            items.add(new TasksAdapter.CategoryHeaderItem(cat));
            
            // Add Tasks for this Category
            for (Task t : tasks) {
                if (t.categoryId == cat.id) {
                    items.add(new TasksAdapter.TaskItem(t));
                }
            }
        }
        
        // Handle tasks with unknown categories (optional, but good practice)
        // For now, only showing categorized tasks as per design "Category -> Line -> Tasks"
        
        mCombinedItems.setValue(items);
    }

    public LiveData<List<TasksAdapter.Item>> getCombinedItems() {
        return mCombinedItems;
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
    
    public void deleteCategory(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // This relies on the DAO access, ideally Repository exposes this
            // But for quick implementation we can use the repository to get DB or add method to Repo
            // Let's add method to Repo in next step or use what we available.
            // Wait, TaskRepository has mCategoryDao private.
             mRepository.deleteCategory(category);
        });
    }

    public void renameCategory(Category category, String newName) {
         category.name = newName;
         mRepository.updateCategory(category);
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
