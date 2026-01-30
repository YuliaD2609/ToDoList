package com.example.todolist.data;

public class NotificationTime {
    public int hour;
    public int minute;
    public boolean isEnabled;

    public NotificationTime(int hour, int minute, boolean isEnabled) {
        this.hour = hour;
        this.minute = minute;
        this.isEnabled = isEnabled;
    }
    
    // Unique ID for PendingIntent
    public int getRequestCode() {
        return hour * 60 + minute; 
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTime that = (NotificationTime) o;
        return hour == that.hour && minute == that.minute;
    }
}
