package com.scanner.bth.http;

import android.os.Environment;
import android.util.Log;

import com.scanner.bth.db.BthLog;
import com.scanner.bth.db.Location;
import com.scanner.bth.db.LocationDevice;
import com.scanner.bth.db.LogEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Api {

    public static final boolean DEMO = true;

    private static final ArrayList<Location> fakeLocations = new ArrayList<>();
    private static final HashMap<Long, ArrayList<LocationDevice>> devicesPerLocation = new HashMap<>();

    static {
        /**
        fakeLocations.add(new Location(1L, "1234 A St", 1));
        fakeLocations.add(new Location(2L, "234 B St", 3));
        fakeLocations.add(new Location(3L, "567 C St", 0));

        devicesPerLocation.put(1L, new ArrayList<LocationDevice>());
        devicesPerLocation.put(2L, new ArrayList<LocationDevice>());
        devicesPerLocation.put(3L, new ArrayList<LocationDevice>());

        //devicesPerLocation.get(1L).add(
                new LocationDevice("12345667890123456789007643213456", 1L, "south-west corner"));
         **/
        getMousetrapStorageDir("data.dat");

    }

    public static boolean uploadLogEntriesForLog(BthLog log, List<LogEntry> entries) {

        for (LogEntry entry : entries) {
            if (!entry.getLogId().toString().contentEquals(log.getUuid().toString())) {
                throw new RuntimeException("attempting to upload a log with a log entry that doesn't belong to it");
            }
        }

        if (DEMO) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static ArrayList<Location> getAllLocations() {
        //getMousetrapStorageDir("gg.data");
        if (DEMO) {

            try {
                Thread.sleep(125);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return fakeLocations;
        }

        // We're going to be in DEMO mode for awhile.
        return null;
    }

    public static ArrayList<LocationDevice> getDevicesForLocation(Location location) {
        if (DEMO) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!devicesPerLocation.containsKey(location.getLocationId())) {
                throw new RuntimeException("trying to get a location that doesn't exist");
            }
            return devicesPerLocation.get(location.getLocationId());
        }

        return null;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static enum ReadState {READ_LOCATION_TABLE, READ_DEVICE_TABLE, BASE};

    public static File getMousetrapStorageDir(String filename) {
        File rootPath = new File(Environment.getExternalStorageDirectory(), "mousetrap");
        if(!rootPath.exists()) {
            boolean worked = rootPath.mkdirs();
            if (worked) {
                Log.d("Api", "success: " + rootPath.getAbsolutePath());
            } else {
                Log.d("Api", "failure: " + rootPath.getAbsolutePath());
            }
        } else {
            Log.d("Api", "path already exists");
            Log.d("Api", rootPath.getAbsolutePath());
            try {
                Log.d("Api", rootPath.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File dataFile = new File(rootPath, filename);

        try {
            Scanner scanner = new Scanner(dataFile);
            ReadState state = ReadState.BASE;
            int lineNum = 0;

            while(scanner.hasNextLine()) {
                lineNum++;
                String line = scanner.nextLine().trim();

                if (line.contentEquals("")) {
                    continue;
                }

                if (line.startsWith("//")) {
                    // A comment has been found.
                    continue;
                }
                if (line.contains("//")) {
                    // A comment somewhere in the line
                    int index = line.indexOf("//");
                    line = line.substring(0, index);
                }

                if (state == ReadState.BASE) {
                    if (!line.startsWith("START")) {
                        throw new RuntimeException("corrupt file given, missing START, line " + lineNum);
                    } else {
                        String [] words = line.split(" ");
                        if ("LocationTable".contentEquals(words[words.length - 1])) {
                            state = ReadState.READ_LOCATION_TABLE;
                            continue;
                        } else if ("DeviceTable".contentEquals(words[words.length - 1])) {
                            state = ReadState.READ_DEVICE_TABLE;
                            continue;
                        } else {
                            throw new RuntimeException("START must have LocationTable or DeviceTable, line " + lineNum);
                        }
                    }
                } else if (state == ReadState.READ_LOCATION_TABLE) {
                    if (line.contentEquals("END")) {
                        state = ReadState.BASE;
                        continue;
                    }

                    String[] values =  line.split(",,");
                    if (values.length > 2) {
                        for (int i = 0; i < values.length; i++) {
                            Log.d("Api", values[i]);
                        }
                        throw new RuntimeException("too many values found in a row, line " + lineNum);
                    }

                    for (int i = 0; i < values.length; i++) {
                        values[i] = values[i].trim();
                    }
                    Location location = new Location(Long.parseLong(values[0]), values[1], 0);
                    fakeLocations.add(location);
                    devicesPerLocation.put(location.getLocationId(), new ArrayList<LocationDevice>());
                    continue;
                } else if (state == ReadState.READ_DEVICE_TABLE) {
                    if (line.contentEquals("END")) {
                        state = ReadState.BASE;
                        continue;
                    }

                    String[] values = line.split(",");
                    if (values.length > 3) {
                        throw new RuntimeException("too many values found in a row, line " + lineNum);
                    }

                    for (int i = 0; i < values.length; i++) {
                        values[i] = values[i].trim();
                    }

                    LocationDevice device = new LocationDevice(values[0], Long.parseLong(values[1]), values[2]);
                    devicesPerLocation.get(device.getLocationId()).add(device);
                }




            }
            /**
             * START
             * // column explanations
             * --
             * --
             * END
             * START
             * // column explanations
             * --
             * --
             * --
             * END
             */
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
