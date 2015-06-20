package com.scanner.bth.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.scanner.bth.CustomApplication;
import com.scanner.bth.auth.AuthHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by shaon on 3/19/2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 12;
    public static final String DATABASE_NAME = "FeedReader.db";

    private static DbHelper instance = null;
    /*private constructor to avoid direct instantiation by other classes*/
    private DbHelper(){
        super(CustomApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }
    /*synchronized method to ensure only 1 instance of LocalDBHelper exists*/
    public static synchronized DbHelper getInstance(){
        if(instance == null){
            instance = new DbHelper();
        }
        return instance;
    }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL(new LogTable().sqlCreateTable());
        db.execSQL(new LogEntryTable().sqlCreateTable());
        db.execSQL(new LocationTable().sqlCreateTable());
        db.execSQL(new LocationDeviceTable().sqlCreateTable());
        db.execSQL(new AccountDetailsTable().sqlCreateTable());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(new LogTable().sqlDeleteTable());
        db.execSQL(new LogEntryTable().sqlDeleteTable());
        db.execSQL(new LocationTable().sqlDeleteTable());
        db.execSQL(new LocationDeviceTable().sqlDeleteTable());
        db.execSQL(new AccountDetailsTable().sqlDeleteTable());

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public List<BthLog> getLogs(Column sortBy, boolean asc) {
        List<BthLog> logList = new ArrayList<BthLog>();
        LogTable table = LogTable.getSingleton();
        // Select All Query
        String [] columns = table.getColumnNamesArray();

        String orderBy = (sortBy == null) ? null : (sortBy.getKey() + (asc == true ? " ASC" : " DESC"));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table.getName(), columns, null, null, null, null, orderBy);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                // Adding contact to list
                logList.add(table.deserialize(cursor));
            } while (cursor.moveToNext());
        }

        // return contact list
        return logList;
    }

    public List<BthLog> getLogsForLocation(Long locationId, Column sortBy, boolean asc) {
        List<BthLog> logList = new ArrayList<BthLog>();
        LogTable table = LogTable.getSingleton();

        String [] columns = table.getColumnNamesArray();
        String select = LogTable.LOCATION_ID.getKey() + "=?";
        String [] selectArgs = {locationId.toString()};

        String orderBy = (sortBy == null) ? null : (sortBy.getKey() + (asc == true ? " ASC" : " DESC"));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table.getName(), columns, select, selectArgs, null, null, orderBy);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                // Adding contact to list
                logList.add(table.deserialize(cursor));
            } while (cursor.moveToNext());
        }

        // return contact list
        return logList;
    }

    public AccountDetails getAccountDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        AccountDetailsTable table = AccountDetailsTable.getSingleton();
        Cursor  cursor = db.rawQuery("select * from " + table.getName(),null);
        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            new RuntimeException("could not get any account details");
        }

        return table.deserialize(cursor);
    }

    public void insertAccountDetails(AccountDetails details) {
        SQLiteDatabase db = this.getWritableDatabase();
        AccountDetailsTable table = AccountDetailsTable.getSingleton();
        db.delete(table.getName(), null, null);
        db.insert(table.getName(), null, table.serialize(details));
    }

    public BthLog getLog(UUID uuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        LogTable table = LogTable.getSingleton();
        Cursor cursor = db.query(table.getName(), table.getColumnNamesArray(), LogTable._UUID.getKey() + "=?",
                new String[] { uuid.toString() }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw new RuntimeException("could not get the requested log: " + uuid.toString());
        }

        BthLog log = LogTable.getSingleton().deserialize(cursor);
        return log;
    }


    public void syncLog(BthLog l) {
        long now = System.currentTimeMillis();
        BthLog dbLog = getLog(l.getUuid());
        SQLiteDatabase db = this.getReadableDatabase();
        LogTable table = LogTable.getSingleton();
        dbLog.setLastSynced(now);
        db.update(table.getName(), table.serialize(dbLog), LogTable._UUID.getKey()+ "=?", new String[] {dbLog.getUuid().toString()});
    }

    public void updateLog(BthLog log) {
        long now = System.currentTimeMillis();
        SQLiteDatabase db = this.getReadableDatabase();
        LogTable table = LogTable.getSingleton();
        log.setLastUpdated(now);
        db.update(table.getName(), table.serialize(log), LogTable._UUID.getKey()+ "=?", new String[] {log.getUuid().toString()});
    }

    public UUID createLog(String owner, Long locationId) {
        long now = System.currentTimeMillis();
        BthLog log = new BthLog(UUID.randomUUID(), now, now, owner, 0L, false, locationId);

        SQLiteDatabase db = this.getWritableDatabase();
        LogTable table = LogTable.getSingleton();
        db.insert(table.getName(), null, table.serialize(log));
        return log.getUuid();
    }

    public void createLogEntry(UUID logId, String byteRecord) {
        long now = System.currentTimeMillis();
        LogEntry entry = new LogEntry(0, logId, byteRecord, 0L, 0L,
                null, null, now, 0L,
                0L, now, null, false, 0L, 100);
        SQLiteDatabase db = this.getWritableDatabase();
        LogEntryTable table = LogEntryTable.getSingleton();
        db.insert(table.getName(), null, table.serialize(entry));

        BthLog log = getLog(logId);
        updateLog(log);

    }

    public void updateLogEntry(LogEntry entry) {
        long now = System.currentTimeMillis();
        entry.setLastUpdated(now);
        SQLiteDatabase db = this.getWritableDatabase();
        LogEntryTable table = LogEntryTable.getSingleton();
        db.update(table.getName(), table.serialize(entry), LogEntryTable._ID.getKey()+"=?",
                new String [] {entry.getId().toString()});

        BthLog log = getLog(entry.getLogId());
        log.setLastUpdated(now);
        updateLog(log);
    }

    public void syncLogEntry(LogEntry e) {
        long now = System.currentTimeMillis();
        LogEntry dbEntry = getLogEntry(e.getId());
        dbEntry.setLastSynced(now);
        SQLiteDatabase db = this.getWritableDatabase();
        LogEntryTable table = LogEntryTable.getSingleton();
        db.update(table.getName(), table.serialize(dbEntry), LogEntryTable._ID.getKey()+"=?",
                new String [] {dbEntry.getId().toString()});
    }

    public List<LogEntry> getLogEntries(BthLog log, Column sortBy, boolean asc) {
        List<LogEntry> logList = new ArrayList<LogEntry>();
        LogEntryTable table = LogEntryTable.getSingleton();
        // Select All Query
        String [] columns = table.getColumnNamesArray();

        String orderBy = (sortBy == null) ? null : (sortBy.getKey() + (asc == true ? " ASC" : " DESC"));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table.getName(), columns, LogEntryTable.LOG_ID.getKey() + "=?",
                new String[] {log.getUuid().toString()}, null, null, orderBy);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                // Adding contact to list
                logList.add(table.deserialize(cursor));
            } while (cursor.moveToNext());
        }

        // return contact list
        return logList;
    }

    public List<Location> getAllLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        LocationTable table = LocationTable.getSingleton();

        // Select All Query
        String [] columns = table.getColumnNamesArray();

        Cursor cursor = db.query(table.getName(), columns, null, null, null, null, null);


        List<Location> results = new ArrayList<Location>();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding contact to list
                results.add(table.deserialize(cursor));
            } while (cursor.moveToNext());
        }

        return results;
    }

    public void addLocations(ArrayList<Location> locations) {
        SQLiteDatabase db = this.getWritableDatabase();
        LocationTable table = LocationTable.getSingleton();

        for (Location location : locations) {
            db.insert(table.getName(), null, table.serialize(location));
        }
    }

    public void deleteAllLocations() {
        SQLiteDatabase db = this.getWritableDatabase();
        LocationTable table = LocationTable.getSingleton();

        db.delete(table.getName(), null, null);
    }

    public void deleteDevicesForLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();
        LocationDeviceTable table = LocationDeviceTable.getSingleton();
        String whereClause = LocationDeviceTable.LOCATION_ID.getKey() + "=?";
        String[] args = {String.valueOf(location.getLocationId())};
        db.delete(table.getName(), whereClause, args);
    }

    public List<LocationDevice> getLocalLocationDevices(Location location) {
        String WHERE = LocationDeviceTable.LOCATION_ID.getKey() + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        LocationDeviceTable table = LocationDeviceTable.getSingleton();

        String [] columns = table.getColumnNamesArray();
        String [] args = {location.getLocationId().toString()};
        Cursor cursor = db.query(table.getName(), columns, WHERE, args, null, null, null);
        List<LocationDevice> results = new ArrayList<>();

        if (cursor == null) {
            throw new RuntimeException("could not get Location Devices");
        }

        if (cursor.moveToFirst()) {
            do {
                results.add(table.deserialize(cursor));
            } while (cursor.moveToNext());
        }
        return results;
    }

    public LocationDevice getLocationDevice(Long locationId, String deviceUuid) {
        String WHERE = LocationDeviceTable.LOCATION_ID.getKey() + "=? AND " + LocationDeviceTable.UUID.getKey() + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        LocationDeviceTable table = LocationDeviceTable.getSingleton();

        Cursor cursor = db.query(table.getName(), table.getColumnNamesArray(), WHERE,
                new String[] { String.valueOf(locationId), deviceUuid }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw new RuntimeException("could not get the requested device: " + deviceUuid.toString());
        }

        LocationDevice log = table.getSingleton().deserialize(cursor);
        return log;
    }

    public void addDevicesToLocation(ArrayList<LocationDevice> devices) {
        SQLiteDatabase db = this.getWritableDatabase();
        LocationDeviceTable table = LocationDeviceTable.getSingleton();

        for (LocationDevice device : devices) {
            db.insert(table.getName(), null, table.serialize(device));
        }
    }

    public LogEntry getLogEntry(Integer logEntryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        LogEntryTable table = LogEntryTable.getSingleton();
        Cursor cursor = db.query(table.getName(), table.getColumnNamesArray(), LogEntryTable._ID.getKey() + "=?",
                new String[] { logEntryId.toString() }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw new RuntimeException("could not get the requested log: " + String.valueOf(logEntryId));
        }

        LogEntry entry = LogEntryTable.getSingleton().deserialize(cursor);
        return entry;
    }

    public Location getLocation(Long locationId) {
        List<Location> locations = getAllLocations();
        Log.d("TAG", "looking for: " + locationId);
        for (Location location: locations) {
            Log.d("TAG", "location id "  + location.getLocationId());
        }
        SQLiteDatabase db = this.getReadableDatabase();
        LocationTable table = LocationTable.getSingleton();

        Cursor cursor = db.query(table.getName(), table.getColumnNamesArray(), LocationTable.LOCATION_ID.getKey() + "=?",
                new String[] { String.valueOf(locationId)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw new RuntimeException("could not get the requested log: " + String.valueOf(locationId));
        }

        Location location = LocationTable.getSingleton().deserialize(cursor);
        return location;
    }

    public void deleteLog(BthLog log) {
        SQLiteDatabase db = this.getWritableDatabase();
        LogTable table = LogTable.getSingleton();
        UUID uuid = log.getUuid();
        int count = db.delete(table.getName(), LogTable._UUID.getKey() + "=?",
                new String[] { uuid.toString() });

        if (count == 0) {
            Log.e(this.getClass().getSimpleName(), "log: " + uuid + " was not found during delete");
        }
    }
}
