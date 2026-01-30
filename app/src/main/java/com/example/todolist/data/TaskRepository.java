package com.example.todolist.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {
    private TaskDao mTaskDao;
    private CategoryDao mCategoryDao;
    private LiveData<List<Task>> mAllTasks;
    private LiveData<List<Category>> mAllCategories;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        mCategoryDao = db.categoryDao();
        mAllTasks = mTaskDao.getAllTasks();
        mAllCategories = mCategoryDao.getAllCategories();
    }

    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    public void insert(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.insert(task);
        });
    }

    public void update(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.update(task);
        });
    }

    public void delete(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.delete(task);
        });
    }

    public void insertCategory(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mCategoryDao.insert(category);
        });
    }

    public void deleteOldCompletedTasks() {
        long cutoff = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours ago
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.deleteOldCompletedTasks(cutoff);
        });
    }
}
