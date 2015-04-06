package com.scanner.bth.db;

/**
 * Created by shaon on 4/2/2015.
 */
public class LocationDevice {
    String uuid;
    Long locationId;

    public LocationDevice(String uuid, Long locationId) {
        this.uuid = uuid;
        this.locationId = locationId;
    }

    public String getUuid() {
        return uuid;
    }

    public Long getLocationId() {
        return locationId;
    }
}
