package com.scanner.bth.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.sb.db.BaseTable;
import com.sb.db.Column;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by shaon on 3/19/2015.
 *
 * Represents a SQL LogTable that can be used to serialize/deserialize Log objects into SQL objects.
 */
public class LogTable extends BaseTable<Log> {

    public static final Column _UUID = new Column("uuid", Column.UUID);
    public static final Column TIME_CREATED = new Column("time_created", Column.LONG);
    public static final Column OWNER = new Column("owner", Column.STRING);
    public static final Column LAST_UPDATED = new Column("last_updated", Column.LONG);
    public static final Column LAST_SYNC = new Column("last_sync", Column.LONG);
    public static final Column FINISHED = new Column("finished", Column.BOOLEAN);
    public static final Column LOCATION_ID = new Column("location_id", Column.LONG).setNotNull();


    private static ArrayList<Column> mColumns = new ArrayList<Column>();
    static {
            mColumns.add(_UUID);
            mColumns.add(TIME_CREATED);
            mColumns.add(LAST_UPDATED);
            mColumns.add(LAST_SYNC);
            mColumns.add(OWNER);
            mColumns.add(FINISHED);
            mColumns.add(LOCATION_ID);
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
        values.put(_UUID.getKey(), obj.getUuid().toString());
        values.put(TIME_CREATED.getKey(), obj.getTimeCreated());
        values.put(LAST_UPDATED.getKey(), obj.getLastUpdated());
        values.put(OWNER.getKey(), obj.getOwner());
        values.put(LAST_SYNC.getKey(), obj.getLastSynced());
        values.put(FINISHED.getKey(), obj.getFinished());
        values.put(LOCATION_ID.getKey(), obj.getLocationId());
        return values;
    }

    @Override
    public Log deserialize(Cursor cursor) {
        UUID id = (UUID) extract(_UUID, cursor);
        Long timeCreated = (Long) extract(TIME_CREATED, cursor);
        Long lastUpdated = (Long) extract(LAST_UPDATED, cursor);
        String owner = (String) extract(OWNER, cursor);
        Long lastSynced = (Long) extract(LAST_SYNC, cursor);
        Boolean finished = (Boolean) extract(FINISHED, cursor);
        Long locationId = (Long) extract(LOCATION_ID, cursor);

        return new Log(id, timeCreated, lastUpdated, owner, lastSynced, finished, locationId);
    }

    public static LogTable getSingleton() {
        if (mSingleton == null) {
            mSingleton = new LogTable();
        }

        return mSingleton;
    }
}
