package com.scanner.bth.db;

public class LogEntry {

    Integer id;
    Integer logId;

    String byteRecord;
    Long deviceLastChecked;
    Long lastMouseEvent;
    String lastSigner;

    String currentSigner;
    Long currentDeviceCheckTime;

    Long timeCreated;
    Long lastUpdated;
    Long lastSynced;

    public LogEntry(Integer id, Integer logId, String byteRecord, Long deviceLastChecked, Long lastMouseEvent,
                    String lastSigner, String currentSigner, Long lastUpdated, Long lastSynced,
                    Long currentDeviceCheckTime, Long timeCreated) {

        this.id = id;
        this.logId = logId;
        this.byteRecord = byteRecord;
        this.deviceLastChecked = deviceLastChecked;
        this.lastMouseEvent = lastMouseEvent;
        this.lastSigner = lastSigner;
        this.currentSigner = currentSigner;
        this.lastUpdated = lastUpdated;
        this.lastSynced = lastSynced;
        this.currentDeviceCheckTime = currentDeviceCheckTime;
        this.timeCreated = timeCreated;
    }


    public Integer getLogId() {
        return logId;
    }

    public String getByteRecord() {
        return byteRecord;
    }

    public Long getDeviceLastChecked() {
        return deviceLastChecked;
    }

    public Long getLastMouseEvent() {
        return lastMouseEvent;
    }

    public String getLastSigner() {
        return lastSigner;
    }

    public String getCurrentSigner() {
        return currentSigner;
    }

    public Long getCurrentDeviceCheckTime() {
        return currentDeviceCheckTime;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public Long getLastSynced() {
        return lastSynced;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public Integer getId() {
        return id;
    }
}
