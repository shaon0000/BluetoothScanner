package com.scanner.bth.http;

import com.scanner.bth.db.Location;
import com.scanner.bth.db.LocationDevice;

import java.util.ArrayList;
import java.util.HashMap;

public class Api {

    public static final boolean DEMO = true;

    private static final ArrayList<Location> fakeLocations = new ArrayList<>();
    private static final HashMap<Location, ArrayList<LocationDevice>> devicesPerLocation = new HashMap<>();

    static {
        fakeLocations.add(new Location(1L, "1234 A St", 1));
        fakeLocations.add(new Location(2L, "234 B St", 3));
        fakeLocations.add(new Location(3L, "567 C St", 0));
        devicesPerLocation.put(fakeLocations.get(0), new ArrayList<LocationDevice>());
        devicesPerLocation.get(fakeLocations.get(0)).add(
                new LocationDevice("12345667890123456789007643213456", 1L));

    }

    public static ArrayList<Location> getAllLocations() {
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

            return devicesPerLocation.get(fakeLocations.get(0));
        }

        return null;
    }
}
