package com.scanner.bth.db;

import java.util.UUID;

public class LogEntry {

    String message;
    Integer id;
    UUID logId;

    String byteRecord;
    Long deviceLastChecked;
    Long lastMouseEvent;
    String lastSigner;

    public Boolean getShouldIgnore() {
        return shouldIgnore;
    }

    public void setShouldIgnore(Boolean shouldIgnore) {
        this.shouldIgnore = shouldIgnore;
    }

    Boolean shouldIgnore;

    public void setByteRecord(String byteRecord) {
        this.byteRecord = byteRecord;
    }

    public void setDeviceLastChecked(Long deviceLastChecked) {
        this.deviceLastChecked = deviceLastChecked;
    }

    public void setLastMouseEvent(Long lastMouseEvent) {
        this.lastMouseEvent = lastMouseEvent;
    }

    public void setLastSigner(String lastSigner) {
        this.lastSigner = lastSigner;
    }

    public void setCurrentSigner(String currentSigner) {
        this.currentSigner = currentSigner;
    }

    public void setCurrentDeviceCheckTime(Long currentDeviceCheckTime) {
        this.currentDeviceCheckTime = currentDeviceCheckTime;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setLastSynced(Long lastSynced) {
        this.lastSynced = lastSynced;
    }

    String currentSigner;

    // The moment when communication happened with the device.
    // This is zero/null otherwise. It can be used to check whether a device
    // was ever found.
    Long currentDeviceCheckTime;

    Long timeCreated;
    Long lastUpdated;
    Long lastSynced;

    public LogEntry(Integer id, UUID logId, String byteRecord, Long deviceLastChecked, Long lastMouseEvent,
                    String lastSigner, String currentSigner, Long lastUpdated, Long lastSynced,
                    Long currentDeviceCheckTime, Long timeCreated, String message, Boolean shouldIgnore) {

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
        this.message = message;
        this.shouldIgnore = shouldIgnore;
    }


    public UUID getLogId() {
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

    public String getMessage() { return message; }

    public void setMessage(String message) {this.message = message; }
}
