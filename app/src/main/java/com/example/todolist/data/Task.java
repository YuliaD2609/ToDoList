package com.example.todolist.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks", foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryId", onDelete = ForeignKey.CASCADE), indices = {
        @Index("categoryId") })
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public boolean isDone;
    public long timestampCreated;
    public long timestampDone; // 0 if not done
    public int categoryId;

    public Task(String name, int categoryId, long timestampCreated) {
        this.name = name;
        this.categoryId = categoryId;
        this.timestampCreated = timestampCreated;
        this.isDone = false;
        this.timestampDone = 0;
    }
}
