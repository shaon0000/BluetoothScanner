package com.scanner.bth.db;

/**
 * Created by shaon on 4/2/2015.
 */
public class LocationDevice {
    String uuid;
    Long locationId;
    String name;

    public LocationDevice(String uuid, Long locationId, String name) {
        this.uuid = uuid;
        this.locationId = locationId;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public Long getLocationId() {
        return locationId;
    }


    public String getName() {
        return name;
    }
}
