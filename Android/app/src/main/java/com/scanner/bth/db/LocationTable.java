package com.scanner.bth.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.sb.db.BaseTable;
import com.sb.db.Column;

import java.util.ArrayList;

public class LocationTable extends BaseTable<Location> {
    public static final Column LOCATION_ID = new Column("location_id", Column.LONG).setNotNull().setUnique();
    public static final Column ADDRESS = new Column("address", Column.STRING);
    public static final Column DEVICE_COUNT = new Column("device_count", Column.INTEGER);

    private static final ArrayList<Column> mColumns = new ArrayList<>();
    static {
        mColumns.add(LOCATION_ID);
        mColumns.add(ADDRESS);
        mColumns.add(DEVICE_COUNT);
    }

    private static LocationTable mSingleton;

    @Override
    public String getName() {
        return "LocationTable";
    }

    @Override
    protected ArrayList<Column> initializeColumns() {
        return mColumns;
    }

    @Override
    public ContentValues serialize(Location obj) {
        ContentValues values = new ContentValues();
        values.put(LOCATION_ID.getKey(), obj.getLocationId());
        values.put(ADDRESS.getKey(), obj.getAddress());
        values.put(DEVICE_COUNT.getKey(), obj.getDeviceCount());
        return values;
    }

    @Override
    public Location deserialize(Cursor cursor) {
        Long locationId = (Long) extract(LOCATION_ID, cursor);
        String address = (String) extract(ADDRESS, cursor);
        Integer deviceCount = (Integer) extract(DEVICE_COUNT, cursor);

        return new Location(locationId, address, deviceCount);
    }


    public static LocationTable getSingleton() {
        if (mSingleton == null) {
            mSingleton = new LocationTable();
        }

        return mSingleton;
    }
}
