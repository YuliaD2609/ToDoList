package com.example.todolist.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Delete
    void delete(Task task);

    @Update
    void update(Task task);

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY isDone ASC, timestampCreated DESC")
    LiveData<List<Task>> getTasksByCategory(int categoryId);

    @Query("SELECT * FROM tasks ORDER BY isDone ASC, timestampCreated DESC")
    LiveData<List<Task>> getAllTasks();

    @Query("DELETE FROM tasks WHERE isDone = 1 AND timestampDone < :cutoffTimestamp")
    void deleteOldCompletedTasks(long cutoffTimestamp);
}
