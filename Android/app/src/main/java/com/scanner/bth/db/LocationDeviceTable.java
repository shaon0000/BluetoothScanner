package com.scanner.bth.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.sb.db.BaseTable;
import com.sb.db.Column;

import java.util.ArrayList;

/**
 * Created by shaon on 4/2/2015.
 */
public class LocationDeviceTable extends BaseTable<LocationDevice> {

    public static final Column UUID = new Column("uuid", Column.STRING);
    public static final Column LOCATION_ID = new Column ("location_id", Column.LONG);

    private static final ArrayList<Column> mColumns = new ArrayList<>();

    static {
        mColumns.add(UUID);
        mColumns.add(LOCATION_ID);
    }

    private static LocationDeviceTable mSingleton;

    @Override
    public String getName() {
        return "LocationDeviceTable";
    }

    @Override
    protected ArrayList<Column> initializeColumns() {
        return mColumns;
    }

    @Override
    public ContentValues serialize(LocationDevice obj) {
        ContentValues values = new ContentValues();
        values.put(UUID.getKey(), obj.getUuid());
        values.put(LOCATION_ID.getKey(), obj.getLocationId());
        return values;
    }

    @Override
    public LocationDevice deserialize(Cursor cursor) {
        String uuid = (String) extract(UUID, cursor);
        Long locationId = (Long) extract(LOCATION_ID, cursor);
        return new LocationDevice(uuid, locationId);
    }


    public static LocationDeviceTable getSingleton() {
        if (mSingleton == null) {
            mSingleton = new LocationDeviceTable();
        }

        return mSingleton;
    }
}
