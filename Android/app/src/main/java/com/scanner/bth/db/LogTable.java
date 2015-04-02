package com.scanner.bth.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.sb.db.BaseTable;
import com.sb.db.Column;

import java.util.ArrayList;

/**
 * Created by shaon on 3/19/2015.
 *
 * Represents a SQL LogTable that can be used to serialize/deserialize Log objects into SQL objects.
 */
public class LogTable extends BaseTable<Log> {

    public static final Column _ID = new Column("_id", Column.PRIMARY_INT_KEY);
    public static final Column TIME_CREATED = new Column("time_created", Column.LONG);
    public static final Column OWNER = new Column("owner", Column.STRING);
    public static final Column LAST_UPDATED = new Column("last_updated", Column.LONG);
    public static final Column LAST_SYNC = new Column("last_sync", Column.LONG);
    public static final Column FINISHED = new Column("finished", Column.BOOLEAN);

    private static ArrayList<Column> mColumns = new ArrayList<Column>();
    static {

            mColumns.add(_ID);
            mColumns.add(TIME_CREATED);
            mColumns.add(LAST_UPDATED);
            mColumns.add(LAST_SYNC);
            mColumns.add(OWNER);
            mColumns.add(FINISHED);

    }
    private static LogTable mSingleton;

    @Override
    public String getName() {
        return "LogTable";
    }

    @Override
    protected ArrayList<Column> initializeColumns() {
        return mColumns;
    }

    @Override
    public ContentValues serialize(Log obj) {
        ContentValues values = new ContentValues();
        values.put(TIME_CREATED.getKey(), obj.getTimeCreated());
        values.put(LAST_UPDATED.getKey(), obj.getLastUpdated());
        values.put(OWNER.getKey(), obj.getOwner());
        values.put(LAST_SYNC.getKey(), obj.getLastSynced());
        values.put(FINISHED.getKey(), obj.getFinished());
        return values;
    }

    @Override
    public Log deserialize(Cursor cursor) {
        Integer id = (Integer) extract(_ID, cursor);
        Long timeCreated = (Long) extract(TIME_CREATED, cursor);
        Long lastUpdated = (Long) extract(LAST_UPDATED, cursor);
        String owner = (String) extract(OWNER, cursor);
        Long lastSynced = (Long) extract(LAST_SYNC, cursor);
        Boolean finished = (Boolean) extract(FINISHED, cursor);

        return new Log(id, timeCreated, lastUpdated, owner, lastSynced, finished);
    }

    public static LogTable getSingleton() {
        if (mSingleton == null) {
            mSingleton = new LogTable();
        }

        return mSingleton;
    }
}
