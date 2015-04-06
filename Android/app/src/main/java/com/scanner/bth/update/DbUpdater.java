package com.scanner.bth.update;

import android.content.Context;

import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.Location;
import com.scanner.bth.db.LocationDevice;
import com.scanner.bth.http.Api;

import java.util.ArrayList;

/**
 * Created by shaon on 4/3/2015.
 */
public class DbUpdater {
    public static void updateDatabaseWithLocations(Context context) {
        ArrayList<Location> locations = Api.getAllLocations();
        DbHelper dbHelper = DbHelper.getInstance();
        dbHelper.deleteAllLocations();
        dbHelper.addLocations(locations);
    }

    public static void updateDatabaseWithDevices(Context context, Location location) {
        ArrayList<LocationDevice> devices = Api.getDevicesForLocation(location);
        DbHelper dbHelper = DbHelper.getInstance();
        dbHelper.deleteDevicesForLocation(location);
        dbHelper.addDevicesToLocation(devices);
    }
}
