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
class LogTable extends BaseTable<Log> {

    public static final Column _ID = new Column("_id", Column.PRIMARY_INT_KEY);
    public static final Column TIME_CREATED = new Column("time_created", Column.LONG);
    public static final Column OWNER = new Column("owner", Column.STRING);
    public static final Column LAST_UPDATED = new Column("last_updated", Column.LONG);

    private ArrayList<Column> mColumns = new ArrayList<Column>();
    static {
        columns.add(_ID);
        columns.add(TIME_CREATED);
        columsn.add(LAST_UPDATED);
        columns.add(OWNER);
    }

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
        return values;
    }

    @Override
    public Log deserialize(Cursor cursor) {
        Integer id = (Integer) extract(_ID, cursor);
        Long timeCreated = (Long) extract(TIME_CREATED, cursor);
        Long lastUpdated = (Long) extract(LAST_UPDATED, cursor);
        String owner = (String) extract(OWNER, cursor);

        return new Log(id, timeCreated, lastUpdated, owner);
    }
}
