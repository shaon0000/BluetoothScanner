package com.scanner.bth.db;

/**
 * Created by shaon on 4/2/2015.
 */
public class Location {
    Long locationId;
    String address;
    Integer deviceCount;

    public Location(Long locationId, String address, Integer deviceCount) {
        this.locationId = locationId;
        this.address = address;
        this.deviceCount = deviceCount;
    }

    public Long getLocationId() {
        return locationId;
    }

    public String getAddress() {
        return address;
    }

    public Integer getDeviceCount() {
        return deviceCount;
    }
}
