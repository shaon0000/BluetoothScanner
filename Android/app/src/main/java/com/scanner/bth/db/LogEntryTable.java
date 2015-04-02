package com.scanner.bth.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.sb.db.BaseTable;
import com.sb.db.Column;

import java.util.ArrayList;

public class LogEntryTable extends BaseTable<LogEntry> {
    private static LogTable mSingleton;

    public static final Column _ID = new Column("_id", Column.PRIMARY_INT_KEY);

    public static final Column LOG_ID = new Column("log_id", Column.INTEGER);
    public static final Column BYTE_RECORD = new Column("byte_record", Column.STRING);

    public static final Column DEVICE_LAST_CHECKED = new Column("device_last_checked", Column.LONG);
    public static final Column LAST_MOUSE_EVENT = new Column("last_mouse_event", Column.LONG);
    public static final Column LAST_SIGNER = new Column("last_signer", Column.STRING);

    public static final Column CURRENT_SIGNER = new Column("current_signer", Column.STRING);
    public static final Column CURRENT_DEVICE_CHECK_TIME = new Column("current_device_check_time", Column.LONG);

    public static final Column LAST_UPDATED = new Column("last_updated", Column.LONG);
    public static final Column LAST_SYNCED = new Column("last_synced", Column.LONG);
    private static final Column TIME_CREATED = new Column("time_created", Column.LONG);

    private static final ArrayList<Column> mColumns = new ArrayList<>();


    static {
        mColumns.add(_ID);
        mColumns.add(LOG_ID);
        mColumns.add(BYTE_RECORD);

        mColumns.add(DEVICE_LAST_CHECKED);
        mColumns.add(LAST_MOUSE_EVENT);

        mColumns.add(LAST_SIGNER);
        mColumns.add(CURRENT_SIGNER);
        mColumns.add(CURRENT_DEVICE_CHECK_TIME);

        mColumns.add(TIME_CREATED);
        mColumns.add(LAST_UPDATED);
        mColumns.add(LAST_SYNCED);
    }


    @Override
    public String getName() {
        return "LogEntryTable";
    }

    @Override
    protected ArrayList<Column> initializeColumns() {
        return mColumns;
    }

    @Override
    public ContentValues serialize(LogEntry obj) {
        ContentValues values = new ContentValues();

        values.put(LOG_ID.getKey(), obj.getLogId());
        values.put(BYTE_RECORD.getKey(), obj.getByteRecord());

        values.put(DEVICE_LAST_CHECKED.getKey(), obj.getDeviceLastChecked());
        values.put(LAST_MOUSE_EVENT.getKey(), obj.getLastMouseEvent());

        values.put(LAST_SIGNER.getKey(), obj.getLastSigner());
        values.put(CURRENT_SIGNER.getKey(), obj.getCurrentSigner());
        values.put(CURRENT_DEVICE_CHECK_TIME.getKey(), obj.getCurrentDeviceCheckTime());

        values.put(TIME_CREATED.getKey(), obj.getTimeCreated());
        values.put(LAST_UPDATED.getKey(), obj.getLastUpdated());
        values.put(LAST_SYNCED.getKey(), obj.getLastSynced());

        return values;
    }

    @Override
    public LogEntry deserialize(Cursor cursor) {
        Integer id = (Integer) extract(_ID, cursor);
        Integer logId = (Integer) extract(LOG_ID, cursor);


        String byteRecord = (String) extract(BYTE_RECORD, cursor);
        Long deviceLastChecked = (Long) extract(DEVICE_LAST_CHECKED, cursor);
        Long lastMouseEvent = (Long) extract(LAST_MOUSE_EVENT, cursor);
        String lastSigner = (String) extract(LAST_SIGNER, cursor);

        String currentSigner = (String) extract(CURRENT_SIGNER, cursor);
        Long currentDeviceCheckTime = (Long) extract(CURRENT_DEVICE_CHECK_TIME, cursor);

        Long timeCreated = (Long) extract(TIME_CREATED, cursor);
        Long lastUpdated = (Long) extract(LAST_UPDATED, cursor);
        Long lastSynced = (Long) extract(LAST_SYNCED, cursor);

        return new LogEntry(id, logId, byteRecord, deviceLastChecked, lastMouseEvent,
                lastSigner, currentSigner, lastUpdated, lastSynced,
                currentDeviceCheckTime, timeCreated);
    }

    public static LogTable getSingleton() {
        if (mSingleton == null) {
            mSingleton = new LogTable();
        }

        return mSingleton;
    }
}
