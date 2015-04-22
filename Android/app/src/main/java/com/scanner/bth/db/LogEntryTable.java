package com.scanner.bth.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.UUID;

public class LogEntryTable extends BaseTable<LogEntry> {
    private static LogEntryTable mSingleton;

    public static final Column _ID = new Column("_id", Column.PRIMARY_INT_KEY);

    public static final Column LOG_ID = new Column("log_id", Column.UUID).setNotNull();
    public static final Column BYTE_RECORD = new Column("byte_record", Column.STRING).setNotNull();

    public static final Column DEVICE_LAST_CHECKED = new Column("device_last_checked", Column.LONG);
    public static final Column LAST_MOUSE_EVENT = new Column("last_mouse_event", Column.LONG);
    public static final Column LAST_SIGNER = new Column("last_signer", Column.STRING);

    public static final Column CURRENT_SIGNER = new Column("current_signer", Column.STRING);
    public static final Column CURRENT_DEVICE_CHECK_TIME = new Column("current_device_check_time", Column.LONG);

    public static final Column LAST_UPDATED = new Column("last_updated", Column.LONG);
    public static final Column LAST_SYNCED = new Column("last_synced", Column.LONG);
    private static final Column TIME_CREATED = new Column("time_created", Column.LONG);

    public static final Column MESSAGE = new Column("message", Column.STRING);

    // If this is true, it indicates that the user thought the device was broken.
    public static final Column SHOULD_IGNORE = new Column("should_ignore", Column.BOOLEAN).setNotNull();

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
        mColumns.add(MESSAGE);

        mColumns.add(SHOULD_IGNORE);
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

        values.put(LOG_ID.getKey(), obj.getLogId().toString());
        values.put(BYTE_RECORD.getKey(), obj.getByteRecord());

        values.put(DEVICE_LAST_CHECKED.getKey(), obj.getDeviceLastChecked());
        values.put(LAST_MOUSE_EVENT.getKey(), obj.getLastMouseEvent());

        values.put(LAST_SIGNER.getKey(), obj.getLastSigner());
        values.put(CURRENT_SIGNER.getKey(), obj.getCurrentSigner());
        values.put(CURRENT_DEVICE_CHECK_TIME.getKey(), obj.getCurrentDeviceCheckTime());

        values.put(TIME_CREATED.getKey(), obj.getTimeCreated());
        values.put(LAST_UPDATED.getKey(), obj.getLastUpdated());
        values.put(LAST_SYNCED.getKey(), obj.getLastSynced());

        values.put(MESSAGE.getKey(), obj.getMessage());
        values.put(SHOULD_IGNORE.getKey(), obj.getShouldIgnore());
        return values;
    }

    @Override
    public LogEntry deserialize(Cursor cursor) {
        Integer id = (Integer) extract(_ID, cursor);
        UUID logId = (UUID) extract(LOG_ID, cursor);


        String byteRecord = (String) extract(BYTE_RECORD, cursor);
        Long deviceLastChecked = (Long) extract(DEVICE_LAST_CHECKED, cursor);
        Long lastMouseEvent = (Long) extract(LAST_MOUSE_EVENT, cursor);
        String lastSigner = (String) extract(LAST_SIGNER, cursor);

        String currentSigner = (String) extract(CURRENT_SIGNER, cursor);
        Long currentDeviceCheckTime = (Long) extract(CURRENT_DEVICE_CHECK_TIME, cursor);

        Long timeCreated = (Long) extract(TIME_CREATED, cursor);
        Long lastUpdated = (Long) extract(LAST_UPDATED, cursor);
        Long lastSynced = (Long) extract(LAST_SYNCED, cursor);

        String message = (String) extract(MESSAGE, cursor);

        Boolean shouldIgnore = (Boolean) extract(SHOULD_IGNORE, cursor);

        return new LogEntry(id, logId, byteRecord, deviceLastChecked, lastMouseEvent,
                lastSigner, currentSigner, lastUpdated, lastSynced,
                currentDeviceCheckTime, timeCreated, message, shouldIgnore);
    }

    public static LogEntryTable getSingleton() {
        if (mSingleton == null) {
            mSingleton = new LogEntryTable();
        }

        return mSingleton;
    }
}
