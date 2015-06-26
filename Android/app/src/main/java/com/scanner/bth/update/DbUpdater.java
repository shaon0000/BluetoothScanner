package com.scanner.bth.update;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.scanner.bth.bluetoothscanner.R;
import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.Location;
import com.scanner.bth.db.LocationDevice;
import com.scanner.bth.http.Api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaon on 4/3/2015.
 */
public class DbUpdater {
    public static void updateDatabaseWithLocations(Context context) {
        ArrayList<Location> locations = Api.getAllLocations();
        Log.d("DbUpdater","locations: " + locations.size());
        DbHelper dbHelper = DbHelper.getInstance();
        dbHelper.deleteAllLocations();
        dbHelper.addLocations(locations);
    }

    public static void updateDatabaseWithDevices(Context context, Location location) {
        ArrayList<LocationDevice> devices = Api.getDevicesForLocation(location);
        for (LocationDevice device: devices) {
            Log.d("DbUpdater", device.getLocationId() + " " + device.getName() + " " + device.getUuid());
        }
        Log.d("DbUpdater","devices for " + location.getAddress() + ": " + devices.size());
        DbHelper.getInstance().deleteDevicesForLocation(location);

        if(DbHelper.getInstance().getLocalLocationDevices(location).size() != 0) {
            throw new RuntimeException("delete failed");
        }

        DbHelper.getInstance().addDevicesToLocation(devices);
        List<LocationDevice> dbDevices = DbHelper.getInstance().getLocalLocationDevices(location);
        int size = dbDevices.size();
        if (size != devices.size()) {
            throw new RuntimeException("we're adding way more devices than we expected");
        }
    }

    public static void updateExternalDirectoryWithImages(Context context) {
        copyResourceToStorage(R.raw.app_logo, "logo.jpg", context);
        copyResourceToStorage(R.raw.sw_logo, "sw_logo.jpg", context);

    }

    public static void copyResourceToStorage(int res, String filename, Context context) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = context.getResources().openRawResource(res);
            File rootPath = new File(Environment.getExternalStorageDirectory(), "mousetrap");
            out = new FileOutputStream(new File(rootPath, filename));
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e(DbUpdater.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
