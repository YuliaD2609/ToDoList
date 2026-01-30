package com.example.todolist.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TaskDao_Impl implements TaskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Task> __insertionAdapterOfTask;

  private final EntityDeletionOrUpdateAdapter<Task> __deletionAdapterOfTask;

  private final EntityDeletionOrUpdateAdapter<Task> __updateAdapterOfTask;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldCompletedTasks;

  public TaskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTask = new EntityInsertionAdapter<Task>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `tasks` (`id`,`name`,`isDone`,`timestampCreated`,`timestampDone`,`categoryId`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Task entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        final int _tmp = entity.isDone ? 1 : 0;
        statement.bindLong(3, _tmp);
        statement.bindLong(4, entity.timestampCreated);
        statement.bindLong(5, entity.timestampDone);
        statement.bindLong(6, entity.categoryId);
      }
    };
    this.__deletionAdapterOfTask = new EntityDeletionOrUpdateAdapter<Task>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tasks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Task entity) {
        statement.bindLong(1, entity.id);
      }
    };
    this.__updateAdapterOfTask = new EntityDeletionOrUpdateAdapter<Task>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tasks` SET `id` = ?,`name` = ?,`isDone` = ?,`timestampCreated` = ?,`timestampDone` = ?,`categoryId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Task entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        final int _tmp = entity.isDone ? 1 : 0;
        statement.bindLong(3, _tmp);
        statement.bindLong(4, entity.timestampCreated);
        statement.bindLong(5, entity.timestampDone);
        statement.bindLong(6, entity.categoryId);
        statement.bindLong(7, entity.id);
      }
    };
    this.__preparedStmtOfDeleteOldCompletedTasks = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tasks WHERE isDone = 1 AND timestampDone < ?";
        return _query;
      }
    };
  }

  @Override
  public void insert(final Task task) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTask.insert(task);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Task task) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfTask.handle(task);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Task task) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfTask.handle(task);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteOldCompletedTasks(final long cutoffTimestamp) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldCompletedTasks.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, cutoffTimestamp);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteOldCompletedTasks.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Task>> getTasksByCategory(final int categoryId) {
    final String _sql = "SELECT * FROM tasks WHERE categoryId = ? ORDER BY isDone ASC, timestampCreated DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, categoryId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks"}, false, new Callable<List<Task>>() {
      @Override
      @Nullable
      public List<Task> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsDone = CursorUtil.getColumnIndexOrThrow(_cursor, "isDone");
          final int _cursorIndexOfTimestampCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampCreated");
          final int _cursorIndexOfTimestampDone = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampDone");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final List<Task> _result = new ArrayList<Task>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Task _item;
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final long _tmpTimestampCreated;
            _tmpTimestampCreated = _cursor.getLong(_cursorIndexOfTimestampCreated);
            final int _tmpCategoryId;
            _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            _item = new Task(_tmpName,_tmpCategoryId,_tmpTimestampCreated);
            _item.id = _cursor.getInt(_cursorIndexOfId);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDone);
            _item.isDone = _tmp != 0;
            _item.timestampDone = _cursor.getLong(_cursorIndexOfTimestampDone);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Task>> getAllTasks() {
    final String _sql = "SELECT * FROM tasks ORDER BY isDone ASC, timestampCreated DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks"}, false, new Callable<List<Task>>() {
      @Override
      @Nullable
      public List<Task> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsDone = CursorUtil.getColumnIndexOrThrow(_cursor, "isDone");
          final int _cursorIndexOfTimestampCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampCreated");
          final int _cursorIndexOfTimestampDone = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampDone");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final List<Task> _result = new ArrayList<Task>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Task _item;
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final long _tmpTimestampCreated;
            _tmpTimestampCreated = _cursor.getLong(_cursorIndexOfTimestampCreated);
            final int _tmpCategoryId;
            _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            _item = new Task(_tmpName,_tmpCategoryId,_tmpTimestampCreated);
            _item.id = _cursor.getInt(_cursorIndexOfId);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDone);
            _item.isDone = _tmp != 0;
            _item.timestampDone = _cursor.getLong(_cursorIndexOfTimestampDone);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
